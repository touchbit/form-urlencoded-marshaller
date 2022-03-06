package org.touchbit.www.form.url.codec.chain;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Interface for storing the structure and values of the form parameter.
 * Stores and processes both flat data and nested objects and lists.
 * Flat: {@code foo=value}
 * Nested map: {@code foo[bar]=value}
 * Nested map with unindexed array: {@code foo[bar][]=value}
 * Nested map with indexed array: {@code foo[bar][0]=value}
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 04.03.2022
 */
public interface IChainPart {

    /**
     * @return converted this {@link IChainPart} to {@link Map} structure (with nesting) - {@code {foo={bar=["value"]}}}
     */
    Map<String, Object> getRawDataValue();

    /**
     * @param part - append key part to the current key (nested key element)
     * @return this {@link IChainPart}
     */
    IChainPart appendPart(String part);

    /**
     * @param index - append array part to the current key (nested array element)
     * @return this {@link IChainPart}
     */
    IChainPart appendIndex(int index);

    /**
     * @param value - form parameter value
     * @return this {@link IChainPart}
     */
    IChainPart setValue(final String value);

    /**
     * @return a full copy of the current instance of {@link IChainPart}
     */
    IChainPart copy();

    /**
     * @return converted key to {@link List} of {@link IChainKey}
     */
    List<IChainKey> getKeyChain();

    /**
     * @return form parameter key
     */
    String getKey();

    /**
     * @return form parameter value
     */
    String getValue();

    /**
     * @return sign of an unindexed array (true)
     */
    boolean isImplicitList();

    /**
     * @return sign of an indexed array (true)
     */
    boolean isExplicitList();

    /**
     * Default {@link IChainPart} impl for storing the structure and values of the form parameter.
     * Stores and processes both flat data and nested objects and lists.
     * Flat: {@code foo=value}
     * Nested map: {@code foo[bar]=value}
     * Nested map with unindexed array: {@code foo[bar][]=value}
     * Nested map with indexed array: {@code foo[bar][0]=value}
     * <p>
     *
     * @author Oleg Shaburov (shaburov.o.a@gmail.com)
     * Created: 04.03.2022
     */
    class Default implements IChainPart {

        private final boolean implicitList;
        private final boolean explicitList;
        private String key;
        private String value;

        /**
         * @param keyChain     - from url encoded parameter string key
         * @param implicitList - key contains an implicit array (unindexed) - {@code foo[bar][]=value}
         * @param explicitList - key contains an explicit array (indexed) - {@code foo[bar][0]=value}
         */
        public Default(String keyChain, final boolean implicitList, final boolean explicitList) {
            this(keyChain, null, implicitList, explicitList);
        }

        /**
         * @param keyChain     - from url encoded parameter string key
         * @param value        - from url encoded parameter string value
         * @param implicitList - key contains an implicit array (unindexed) - {@code foo[bar][]=value}
         * @param explicitList - key contains an explicit array (indexed) - {@code foo[bar][0]=value}
         */
        public Default(String keyChain, String value, final boolean implicitList, final boolean explicitList) {
            this.implicitList = implicitList;
            this.explicitList = explicitList;
            this.key = keyChain;
            this.value = value;
        }

        /**
         * @return converted this {@link Default} to {@link Map} structure (with nesting) - {@code {foo={bar=["value"]}}}
         * @throws IllegalArgumentException if first key element is array item
         */
        @Override
        @SuppressWarnings("unchecked")
        public Map<String, Object> getRawDataValue() {
            final List<IChainKey> keyChain = getKeyChain();
            final ListIterator<IChainKey> iterator = keyChain.listIterator(keyChain.size());
            Object nested = null;
            boolean isLast = true;
            while (iterator.hasPrevious()) {
                final IChainKey previous = iterator.previous();
                if (previous.isMap()) {
                    final Map<String, Object> tempMap = new HashMap<>();
                    if (isLast) {
                        tempMap.put(previous.getKeyName(), value);
                        isLast = false;
                    } else {
                        tempMap.put(previous.getKeyName(), nested);
                    }
                    nested = tempMap;
                } else {
                    final IChainList chainList;
                    if (previous.isIndexedList()) {
                        chainList = new IChainList.Default(true);
                        // go around java.lang.IndexOutOfBoundsException: Index: 1, Size: 0
                        // Store the value strictly by index (null value)
                        for (int i = 0; i < previous.getIndex(); i++) {
                            chainList.add(i, null);
                        }
                        if (isLast) {
                            chainList.add(previous.getIndex(), value);
                            isLast = false;
                        } else {
                            chainList.add(previous.getIndex(), nested);
                        }
                    } else {
                        chainList = new IChainList.Default(false);
                        if (isLast) {
                            chainList.add(value);
                            isLast = false;
                        } else {
                            chainList.add(nested);
                        }
                    }
                    nested = chainList;
                }
            }
            if (nested instanceof Map) {
                return (Map<String, Object>) nested;
            }
            throw new IllegalArgumentException("Unable to process key. The key does not belong to the 'Map' type.\n" +
                                               "Key: " + key + "\n" +
                                               "Key type: " + (nested == null ? null : nested.getClass()) + "\n" +
                                               "Key structure: " + nested + "\n");
        }

        /**
         * @return converted {@link Default#key} to {@link List} of {@link IChainKey}
         */
        @Override
        public List<IChainKey> getKeyChain() {
            return Arrays.stream(key.split("\\["))
                    .map(k -> k.replace("]", "").trim())
                    .map(IChainKey.Default::new)
                    .collect(Collectors.toList());
        }

        /**
         * @param part - append key part to the {@link Default#key} (nested key element)
         * @return this {@link Default}
         * @throws IllegalArgumentException if value already set (unmodifiable key)
         */
        @Override
        public Default appendPart(String part) {
            if (value != null) {
                throw new IllegalArgumentException("It is forbidden to change the key if the value is already set.\n" +
                                                   "Key: " + key + "\n" +
                                                   "Val: " + value + "\n" +
                                                   "Wrong part: " + part + "\n");
            }
            key += "[" + part + "]";
            return this;
        }

        /**
         * @param index - append array part to the {@link Default#key} (nested array element)
         * @return this {@link Default}
         * @throws IllegalArgumentException if index is negative
         */
        @Override
        public Default appendIndex(int index) {
            if (index < 0) {
                throw new IllegalArgumentException("Array index cannot be negative but got " + index);
            }
            if (explicitList) {
                key += "[" + index + "]";
            } else if (implicitList) {
                key += "[]";
            }
            return this; // hidden
        }

        /**
         * @return a full copy of the current instance of {@link Default}
         */
        @Override
        public Default copy() {
            return new Default(key, value, implicitList, explicitList);
        }

        /**
         * @param value - {@link Default#value}
         * @return this {@link Default}
         */
        @Override
        public Default setValue(final String value) {
            this.value = value;
            return this;
        }

        /**
         * @return {@link Default#key}
         */
        @Override
        public String getKey() {
            return key;
        }

        /**
         * @return {@link Default#value}
         */
        @Override
        public String getValue() {
            return value;
        }

        /**
         * @return {@link Default#implicitList}
         */
        @Override
        public boolean isImplicitList() {
            return implicitList;
        }

        /**
         * @return {@link Default#explicitList}
         */
        @Override
        public boolean isExplicitList() {
            return explicitList;
        }

        /**
         * @return key-value pair in URL-encoded form format
         */
        @Override
        public String toString() {
            return key + "=" + (value == null ? "<null>" : value);
        }
    }
}
