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

package org.touchbit.www.form.url.codec.chain;

import org.apache.commons.lang3.math.NumberUtils;
import org.touchbit.www.form.url.codec.FormUrlUtils;

import java.util.*;

import static org.touchbit.www.form.url.codec.CodecConstant.*;

/**
 * Interface for storing a chain of from url encoded parameters
 * in the list view of {@link IChainPart}
 * and raw representation of the {@link Map} class
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 05.03.2022
 */
public interface IChain {

    /**
     * @return list of from url encoded parameters
     */
    List<IChainPart> getChainParts();

    /**
     * @return from url encoded parameters raw representation
     */
    Map<String, Object> getRawData();

    /**
     * Default {@link IChain} implementation
     */
    class Default implements IChain {

        /**
         * chain raw data
         */
        private final Map<String, Object> rawData;

        /**
         * chain data represents by {@link IChainPart} list
         */
        private final List<IChainPart> chainParts;

        /**
         * Chaining from raw data
         *
         * @param rawData      - from url encoded parameters raw representation
         * @param implicitList - key contains an implicit array (unindexed) - {@code foo[bar][]=value}
         * @param explicitList - key contains an explicit array (indexed) - {@code foo[bar][0]=value}
         * @throws NullPointerException - rawData is null
         */
        public Default(final Map<String, Object> rawData,
                       final boolean implicitList,
                       final boolean explicitList) {
            FormUrlUtils.parameterRequireNonNull(rawData, RAW_DATA_PARAMETER);
            this.rawData = rawData;
            if (rawData.isEmpty()) {
                this.chainParts = new ArrayList<>();
            } else {
                this.chainParts = readModel(rawData, implicitList, explicitList);
            }
        }

        /**
         * Chaining from FormUrlEncoded string data
         * For example: {@code foo[bar][0]=value1&foo[bar][1]=value2}
         *
         * @param urlEncodedString - from url encoded parameters ({@code foo[bar][0]=value1&foo[bar][1]=value2})
         */
        public Default(final String urlEncodedString) {
            if (urlEncodedString == null || urlEncodedString.trim().length() == 0) {
                this.chainParts = new ArrayList<>();
                this.rawData = new HashMap<>();
            } else {
                this.chainParts = readUrlEncodedString(urlEncodedString);
                this.rawData = chainPartsToRawData(this.chainParts);
            }
        }

        /**
         * The method converts the {@link IChainPart} list into
         * a raw representation ({@link Map}) with nesting and values
         *
         * @param list - form data chain parts list
         * @return filled raw data
         */
        protected Map<String, Object> chainPartsToRawData(final List<IChainPart> list) {
            final Map<String, Object> result = new HashMap<>();
            list.stream()
                    .map(IChainPart::getRawDataValue)
                    .forEach(field -> mergeRawMap(field, result));
            return result;
        }

        /**
         * Method for {@link Map} merging
         * If the source and the target have the same keys,
         * then the internal objects will be merged
         *
         * @param source - source {@link Map} for merging
         * @param target - target {@link Map} for merging
         * @return - merged target {@link Map}
         * @throws NullPointerException     if source or target is null
         * @throws IllegalArgumentException incompatible types
         */
        @SuppressWarnings("unchecked")
        protected Map<String, Object> mergeRawMap(final Object source, final Object target) {
            FormUrlUtils.parameterRequireNonNull(source, SOURCE_PARAMETER);
            FormUrlUtils.parameterRequireNonNull(target, TARGET_PARAMETER);
            if (!(source instanceof Map && target instanceof Map)) {
                throw new IllegalArgumentException("Received incompatible types to merge\n" +
                                                   "Expected type: " + Map.class + "\n" +
                                                   "Actual source: " + source.getClass() + "\n" +
                                                   "Actual target: " + target.getClass() + "\n");
            }
            final Map<String, Object> sourceMap = (Map<String, Object>) source;
            final Map<String, Object> targetMap = (Map<String, Object>) target;
            sourceMap.keySet()
                    .forEach(key -> targetMap.merge(key, sourceMap.get(key), (targetValue, sourceValue) -> {
                        if (targetValue instanceof IChainList && sourceValue instanceof IChainList) {
                            return mergeRawList(sourceValue, targetValue);
                        } else if (targetValue instanceof Map && sourceValue instanceof Map) {
                            return mergeRawMap(sourceValue, targetValue);
                        }
                        throw new IllegalArgumentException("Received incompatible value types to merge.\n" +
                                                           "Source: " + sourceValue + "\n" +
                                                           "Target: " + targetValue + "\n");
                    }));
            return targetMap;
        }

        /**
         * Method for {@link IChainList} merging
         * For indexed form list
         * - Set by index - target null value is replaced by source value at the corresponding index
         * - Merge by index - target value is merged by source value at the corresponding index
         * For unindexed form list
         * - If list of maps - merge maps and set to target collection by corresponding index
         * - else appends all elements in the target collection
         *
         * @param source source {@link IChainList} for merging
         * @param target target {@link IChainList} for merging
         * @return merged target {@link IChainList}
         * @throws NullPointerException     if source or target is null
         * @throws IllegalArgumentException incompatible types
         */
        protected IChainList mergeRawList(final Object source, final Object target) {
            FormUrlUtils.parameterRequireNonNull(source, SOURCE_PARAMETER);
            FormUrlUtils.parameterRequireNonNull(target, TARGET_PARAMETER);
            if (!(source instanceof IChainList && target instanceof IChainList)) {
                throw new IllegalArgumentException("Received incompatible types to merge\n" +
                                                   "Expected type: " + IChainList.class + "\n" +
                                                   "Actual source: " + source.getClass() + "\n" +
                                                   "Actual target: " + target.getClass() + "\n");
            }
            final IChainList sourceList = (IChainList) source;
            final IChainList targetList = (IChainList) target;
            if (sourceList.isEmpty() || targetList.isEmpty()) {
                return sourceList.isEmpty() ? targetList : sourceList;
            }
            if (sourceList.size() < targetList.size()) {
                return mergeRawList(targetList, sourceList); // reverse
            }
            if (sourceList.isNotIndexed() && targetList.isNotIndexed()) {
                if (sourceList.get(0) instanceof Map) {
                    targetList.set(0, mergeRawMap(sourceList.get(0), targetList.get(0)));
                } else {
                    targetList.addAll(sourceList);
                }
                return targetList;
            }
            if (sourceList.isNotFilled()) {
                if (sourceList.size() <= targetList.size()) {
                    sourceList.stream() // insert map to list by index
                            .filter(Map.class::isInstance)
                            .map(sourceList::indexOf)
                            .forEach(i -> sourceList.set(i, mergeRawMap(sourceList.get(i), targetList.get(i))));
                }
                targetList.stream() // merge not inserted objects (map/list/simple)
                        .map(v -> v == null ? null : targetList.indexOf(v))
                        .filter(Objects::nonNull)
                        .forEach(i -> sourceList.set(i, targetList.get(i)));
                return sourceList;
            } else { // merge maps
                targetList.set(0, mergeRawMap(sourceList.get(0), targetList.get(0)));
                return targetList;
            }
        }

        /**
         * Transforms from url encoded parameters string to list of separated {@link IChainPart} (key/value pairs)
         *
         * @param urlEncodedString - from url encoded parameters ({@code foo[bar][0]=value1&foo[bar][1]=value2})
         * @return form data {@link IChainPart} where part contains one key/value pair ({@code foo[bar][0]=value1})
         * @throws IllegalArgumentException key-value pair is not in URL form format
         */
        protected List<IChainPart> readUrlEncodedString(final String urlEncodedString) {
            if (urlEncodedString == null || urlEncodedString.trim().isEmpty()) {
                return new ArrayList<>();
            }
            final String[] pairs = urlEncodedString.split("&");
            List<IChainPart> result = new ArrayList<>();
            for (String pair : pairs) {
                final String[] split = pair.contains("=") ? pair.split("=") : new String[]{};
                if (split.length > 2 || split.length == 0) {
                    throw new IllegalArgumentException("URL encoded key-value pair is not in URL format:\n" +
                                                       "Pair: " + urlEncodedString);
                }
                final String rawValue = (split.length == 1) ? "" : split[1].trim();
                final String rawKey = split[0].trim();
                assertKeyBrackets(rawKey);
                final boolean implicitList = rawKey.contains("[]");
                final boolean explicitList = Arrays.stream(rawKey.split("\\["))
                        .map(p -> p.replace("]", ""))
                        .anyMatch(NumberUtils::isDigits);
                result.add(new IChainPart.Default(rawKey, rawValue, implicitList, explicitList));
            }
            return result;
        }

        /**
         * @param key url form parameter key
         * @throws NullPointerException     if key is null
         * @throws IllegalArgumentException incorrect ratio of opening and closing brackets
         * @throws IllegalArgumentException list nesting [[]]
         */
        protected void assertKeyBrackets(String key) {
            FormUrlUtils.parameterRequireNonNull(key, KEY_PARAMETER);
            if (!isEvenBracketsRatio(key)) {
                throw new IllegalArgumentException("The key contains an incorrect ratio of opening and closing brackets.\n" +
                                                   "Invalid key: " + key + "\n");
            }
            if (hasNestedBrackets(key)) {
                throw new IllegalArgumentException("Key nesting is not allowed.\n" +
                                                   "Invalid key: " + key + "\n" +
                                                   "Expected nested object format: filter[foo][bar]\n" +
                                                   "Expected nested list format: filter[foo][0]\n");
            }
        }

        /**
         * @param key url form parameter key
         * @return true if the key contains nested lists
         * @throws NullPointerException if key is null
         */
        protected boolean hasNestedBrackets(String key) {
            FormUrlUtils.parameterRequireNonNull(key, KEY_PARAMETER);
            return key.contains("]]") || key.contains("[[");
        }

        /**
         * @param key url form parameter key
         * @return true if the key contains the same number of opening and closing brackets
         * @throws NullPointerException if key is null
         */
        protected boolean isEvenBracketsRatio(String key) {
            FormUrlUtils.parameterRequireNonNull(key, KEY_PARAMETER);
            final Deque<Character> deque = new LinkedList<>();
            final Map<Character, Character> supported = Collections.singletonMap(']', '[');
            for (char c : key.toCharArray()) {
                if (supported.containsKey(c) && (deque.isEmpty() || !deque.pop().equals(supported.get(c)))) {
                    return false;
                }
                if (supported.containsValue(c)) {
                    deque.push(c);
                }
            }
            return deque.isEmpty();
        }

        /**
         * @param rawData      - from url encoded parameters raw representation
         * @param implicitList - key contains an implicit array (unindexed) - {@code foo[bar][]=value}
         * @param explicitList - key contains an explicit array (indexed) - {@code foo[bar][0]=value}
         * @return list of {@link IChainPart} formed from the received raw data
         */
        protected List<IChainPart> readModel(final Map<String, Object> rawData,
                                             final boolean implicitList,
                                             final boolean explicitList) {
            FormUrlUtils.parameterRequireNonNull(rawData, RAW_DATA_PARAMETER);
            final List<IChainPart> result = new ArrayList<>();
            for (Map.Entry<String, Object> entry : rawData.entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();
                final IChainPart.Default chainPart = new IChainPart.Default(key, implicitList, explicitList);
                result.addAll(valueObjectToChainParts(chainPart, value));
            }
            return result;
        }

        /**
         * Method for converting an object to an IChainPart list based on the parent IChainPart
         * For example:
         * parent IChainPart = {@code foo=null}
         * value is a Map object = {@code {bar=value}} (nested)
         * the method will return a list with one IChainPart - {@code foo[bar]=value}
         *
         * @param chainPart - parent {@link IChainPart}
         * @param value     - object to convert to {@link IChainPart} list
         * @return child {@link IChainPart} list
         * @throws NullPointerException     - chainPart or value is null
         * @throws IllegalArgumentException - unsupported value type
         */
        protected List<IChainPart> valueObjectToChainParts(final IChainPart chainPart, Object value) {
            FormUrlUtils.parameterRequireNonNull(chainPart, CHAIN_PART_PARAMETER);
            FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
            final List<IChainPart> result = new ArrayList<>();
            if (FormUrlUtils.isSimple(value)) {
                result.add(simpleToChainPart(chainPart, value));
            } else if (FormUrlUtils.isCollection(value)) {
                final List<IChainPart> collectionChainParts = collectionToChainPart(chainPart, (Collection<?>) value);
                result.addAll(collectionChainParts);
            } else if (FormUrlUtils.isMap(value)) {
                final List<IChainPart> mapChainParts = mapToChainPart(chainPart, (Map<?, ?>) value);
                result.addAll(mapChainParts);
            } else {
                throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getName());
            }
            return result;
        }

        /**
         * Method for converting a Map to an IChainPart list based on the parent IChainPart
         * For example:
         * parent IChainPart = {@code foo=null}
         * value is a Map object = {@code {bar=value}} (nested)
         * the method will return a list with one IChainPart - {@code foo[bar]=value}
         *
         * @param chainPart - parent {@link IChainPart}
         * @param value     - map to convert to {@link IChainPart} list
         * @return child {@link IChainPart} list
         * @throws NullPointerException - chainPart or value is null
         */
        protected List<IChainPart> mapToChainPart(IChainPart chainPart, Map<?, ?> value) {
            FormUrlUtils.parameterRequireNonNull(chainPart, "chainPart");
            FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
            final List<IChainPart> result = new ArrayList<>();
            for (Map.Entry<?, ?> entry : value.entrySet()) {
                final String eKey = String.valueOf(entry.getKey());
                final Object eValue = entry.getValue() == null ? "" : entry.getValue();
                final IChainPart mapEntryChainPart = chainPart.copy().appendPart(eKey);
                final List<IChainPart> parts = valueObjectToChainParts(mapEntryChainPart, eValue);
                result.addAll(parts);
            }
            return result;
        }

        /**
         * Method for converting a simple type value to an IChainPart
         * For example:
         * parent IChainPart = {@code foo=null}
         * value is a String = 'value'
         * the method will return an IChainPart - {@code foo=value}
         *
         * @param chainPart - parent {@link IChainPart}
         * @param value     - value for chainPart
         * @return chainPart ({@link IChainPart})
         * @throws NullPointerException - chainPart or value is null
         */
        protected IChainPart simpleToChainPart(IChainPart chainPart, final Object value) {
            FormUrlUtils.parameterRequireNonNull(chainPart, CHAIN_PART_PARAMETER);
            FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
            return chainPart.setValue(String.valueOf(value));
        }

        /**
         * Method for converting a list to an IChainPart list based on the parent IChainPart
         * For example:
         * parent IChainPart = {@code foo=null}
         * value is a List object = {@code [value]} (nested)
         * the method will return a list with one IChainPart - {@code foo[0]=value}
         *
         * @param chainPart - parent {@link IChainPart}
         * @param value     - list to convert to {@link IChainPart} list
         * @return child {@link IChainPart} list
         * @throws NullPointerException - chainPart or value is null
         */
        protected List<IChainPart> collectionToChainPart(IChainPart chainPart, final Collection<?> value) {
            FormUrlUtils.parameterRequireNonNull(chainPart, CHAIN_PART_PARAMETER);
            FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
            final List<IChainPart> result = new ArrayList<>();
            final List<?> list;
            if (value instanceof List) {
                list = (List<?>) value;
            } else {
                list = new ArrayList<>(value);
            }
            for (Object item : list) {
                if (item != null) {
                    final IChainPart listChain = chainPart.copy().appendIndex(list.indexOf(item));
                    final List<IChainPart> itemChainParts = valueObjectToChainParts(listChain, item);
                    result.addAll(itemChainParts);
                }
            }
            return result;
        }

        /**
         * @return {@link IChain#getChainParts()}
         */
        @Override
        public List<IChainPart> getChainParts() {
            return chainParts;
        }

        /**
         * @return {@link IChain#getRawData()}
         */
        @Override
        public Map<String, Object> getRawData() {
            return rawData;
        }

        /**
         * @return URL form data (without value encoding/decoding)
         */
        @Override
        public String toString() {
            return toString(false);
        }

        /**
         * @return pretty printed URL form data (without value encoding/decoding)
         */
        public String toString(boolean prettyPrint) {
            StringJoiner sj = new StringJoiner(prettyPrint ? "&\n" : "&");
            getChainParts().forEach(e -> sj.add(e.toString()));
            return sj.toString();
        }
    }
}
