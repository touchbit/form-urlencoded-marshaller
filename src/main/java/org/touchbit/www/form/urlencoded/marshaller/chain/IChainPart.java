/*
 * Copyright 2022 Shaburov Oleg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.touchbit.www.form.urlencoded.marshaller.chain;

import org.touchbit.www.form.urlencoded.marshaller.util.ChainException;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
     * @param part append key part to the current key (nested key element)
     * @return this {@link IChainPart}
     */
    IChainPart appendPart(String part);

    /**
     * @param index append array part to the current key (nested array element)
     * @return this {@link IChainPart}
     */
    IChainPart appendIndex(int index);

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
     * @param value form parameter value
     * @return this {@link IChainPart}
     */
    IChainPart setValue(final String value);

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
         * @param keyChain     from url encoded parameter string key
         * @param implicitList key contains an implicit array (unindexed) {@code foo[bar][]=value}
         * @param explicitList key contains an explicit array (indexed) {@code foo[bar][0]=value}
         */
        public Default(String keyChain, final boolean implicitList, final boolean explicitList) {
            this(keyChain, null, implicitList, explicitList);
        }

        /**
         * @param keyChain     from url encoded parameter string key
         * @param value        from url encoded parameter string value
         * @param implicitList key contains an implicit array (unindexed) {@code foo[bar][]=value}
         * @param explicitList key contains an explicit array (indexed) {@code foo[bar][0]=value}
         */
        public Default(String keyChain, String value, final boolean implicitList, final boolean explicitList) {
            this.implicitList = implicitList;
            this.explicitList = explicitList;
            this.key = keyChain;
            this.value = value;
        }

        /**
         * @return converted this {@link Default} to {@link Map} structure (with nesting) - {@code {foo={bar=["value"]}}}
         * @throws ChainException if first key element is array item
         */
        @Override
        @SuppressWarnings({"unchecked", "java:S3776"})
        public Map<String, Object> getRawDataValue() {
            final List<IChainKey> keyChain = getKeyChain();
            final ListIterator<IChainKey> iterator = keyChain.listIterator(keyChain.size());
            final AtomicBoolean isLast = new AtomicBoolean(true);
            Object nested = null;
            while (iterator.hasPrevious()) {
                final IChainKey previous = iterator.previous();
                if (previous.isMap()) {
                    final Map<String, Object> tempMap = new HashMap<>();
                    if (isLast.compareAndSet(true, false)) {
                        tempMap.put(previous.getKeyName(), value);
                    } else {
                        tempMap.put(previous.getKeyName(), nested);
                    }
                    nested = tempMap;
                } else {
                    final boolean isIndexed = previous.isIndexedList();
                    final IChainList chainList = getNewIChainList(isIndexed);
                    if (isIndexed) {
                        // go around java.lang.IndexOutOfBoundsException: Index: 1, Size: 0
                        // Store the value strictly by index (null value)
                        for (int i = 0; i < previous.getIndex(); i++) {
                            chainList.add(i, null);
                        }
                        if (isLast.compareAndSet(true, false)) {
                            chainList.add(previous.getIndex(), value);
                        } else {
                            chainList.add(previous.getIndex(), nested);
                        }
                    } else {
                        if (isLast.compareAndSet(true, false)) {
                            chainList.add(value);
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
            throw new ChainException("Unable to process key. The key does not belong to the 'Map' type.\n" +
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
         * @param part append key part to the {@link Default#key} (nested key element)
         * @return this {@link Default}
         * @throws ChainException if value already set (unmodifiable key)
         */
        @Override
        public Default appendPart(String part) {
            if (value != null) {
                throw new ChainException("It is forbidden to change the key if the value is already set.\n" +
                                         "Key: " + key + "\n" +
                                         "Val: " + value + "\n" +
                                         "Wrong part: " + part + "\n");
            }
            key += "[" + part + "]";
            return this;
        }

        /**
         * @param index append array part to the {@link Default#key} (nested array element)
         * @return this {@link Default}
         * @throws ChainException if index is negative
         */
        @Override
        public Default appendIndex(int index) {
            if (index < 0) {
                throw new ChainException("Array index cannot be negative but got " + index);
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
         * @param value {@link Default#value}
         * @return this {@link Default}
         */
        @Override
        public Default setValue(final String value) {
            this.value = value;
            return this;
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

        /**
         * @param isIndexed sign that form array is indexed
         * @param values    list values
         * @return new instance of {@link IChainList}
         */
        protected IChainList getNewIChainList(final boolean isIndexed, final Object... values) {
            final IChainList.Default list = new IChainList.Default(isIndexed);
            list.addAll(Arrays.asList(values));
            return list;
        }

    }
}
