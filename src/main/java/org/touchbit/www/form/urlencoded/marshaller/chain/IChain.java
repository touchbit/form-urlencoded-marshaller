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

import org.apache.commons.lang3.math.NumberUtils;
import org.touchbit.www.form.urlencoded.marshaller.util.ChainException;
import org.touchbit.www.form.urlencoded.marshaller.util.CodecConstant;
import org.touchbit.www.form.urlencoded.marshaller.util.FormUrlUtils;
import org.touchbit.www.form.urlencoded.marshaller.util.MarshallerException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.touchbit.www.form.urlencoded.marshaller.util.CodecConstant.ERR_SIMPLE_REFERENCE_TYPES;

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
         * @param rawData      from url encoded parameters raw representation
         * @param implicitList key contains an implicit array (unindexed) {@code foo[bar][]=value}
         * @param explicitList key contains an explicit array (indexed) {@code foo[bar][0]=value}
         * @throws MarshallerException - rawData is null
         */
        public Default(final Map<String, Object> rawData,
                       final boolean implicitList,
                       final boolean explicitList) {
            FormUrlUtils.parameterRequireNonNull(rawData, CodecConstant.RAW_DATA_PARAMETER);
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
         * @param urlEncodedString from url encoded parameters ({@code foo[bar][0]=value1&foo[bar][1]=value2})
         */
        public Default(final String urlEncodedString) {
            this(urlEncodedString, StandardCharsets.UTF_8);
        }

        /**
         * Chaining from FormUrlEncoded string data
         * For example: {@code foo[bar][0]=value1&foo[bar][1]=value2}
         *
         * @param urlEncodedString from url encoded parameters ({@code foo[bar][0]=value1&foo[bar][1]=value2})
         * @param codingCharset    URL form data coding charset
         *                         According to the 3W specification, it is strongly recommended to use
         *                         UTF-8 charset for URL form data coding.
         */
        public Default(final String urlEncodedString, final Charset codingCharset) {
            if (urlEncodedString == null || urlEncodedString.trim().length() == 0) {
                this.chainParts = new ArrayList<>();
                this.rawData = new HashMap<>();
            } else {
                this.chainParts = readUrlEncodedString(urlEncodedString, codingCharset);
                this.rawData = chainPartsToRawData(this.chainParts);
            }
        }

        /**
         * The method converts the {@link IChainPart} list into
         * a raw representation ({@link Map}) with nesting and values
         *
         * @param list form data chain parts list
         * @return filled raw data
         */
        protected Map<String, Object> chainPartsToRawData(final List<IChainPart> list) {
            FormUrlUtils.parameterRequireNonNull(list, CodecConstant.LIST_PARAMETER);
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
         * @param source source {@link Map} for merging
         * @param target target {@link Map} for merging
         * @return - merged target {@link Map}
         * @throws MarshallerException if source or target is null
         */
        @SuppressWarnings("unchecked")
        protected Map<String, Object> mergeRawMap(final Object source, final Object target) {
            FormUrlUtils.parameterRequireNonNull(source, CodecConstant.SOURCE_PARAMETER);
            FormUrlUtils.parameterRequireNonNull(target, CodecConstant.TARGET_PARAMETER);
            if (!(source instanceof Map && target instanceof Map)) {
                throw new ChainException("Received incompatible types to merge\n" +
                                         "Expected type: " + Map.class + "\n" +
                                         "Actual source: " + source.getClass() + "\n" +
                                         "Actual target: " + target.getClass() + "\n");
            }
            final Map<String, Object> sourceMap = (Map<String, Object>) source;
            final Map<String, Object> targetMap = (Map<String, Object>) target;
            sourceMap.keySet().forEach(key -> targetMap
                    .merge(key, sourceMap.get(key), (tValue, sValue) -> mergeObjectValues(sValue, tValue)));
            return targetMap;
        }

        /**
         * Method for merging Map or List or Simple values
         *
         * @param source source value for merging
         * @param target target value for merging
         * @return merge result (Map or List)
         * @throws MarshallerException if source or target is null
         * @throws ChainException      incompatible types
         */
        protected Object mergeObjectValues(final Object source, final Object target) {
            FormUrlUtils.parameterRequireNonNull(source, CodecConstant.SOURCE_PARAMETER);
            FormUrlUtils.parameterRequireNonNull(target, CodecConstant.TARGET_PARAMETER);
            if (FormUrlUtils.isChainList(target) && FormUrlUtils.isChainList(source)) {
                return mergeIChainLists(source, target);
            } else if (FormUrlUtils.isMapAssignableFrom(target) && FormUrlUtils.isMapAssignableFrom(source)) {
                return mergeRawMap(source, target);
            } else if (FormUrlUtils.isSimple(target) && FormUrlUtils.isSimple(source)) {
                // Fires when map keys match and points to a hidden list
                // Example: foo=bar&foo=car -> foo=[bar, car]
                return getNewIChainList(true, target, source);
            } else if (FormUrlUtils.isSimple(source) && FormUrlUtils.isChainList(target)) {
                final boolean notIndexed = ((IChainList) target).isNotIndexed();
                return mergeIChainLists(getNewIChainList(!notIndexed, source), target);
            } else if (FormUrlUtils.isSimple(target) && FormUrlUtils.isChainList(source)) {
                final boolean notIndexed = ((IChainList) source).isNotIndexed();
                return mergeIChainLists(source, getNewIChainList(!notIndexed, target));
            }
            throw new ChainException("Received incompatible value types to merge.\n" +
                                     "Source type: " + source.getClass().getName() + "\n" +
                                     "Source value: " + source + "\n" +
                                     "Target type: " + target.getClass().getName() + "\n" +
                                     "Target value: " + target + "\n");
        }

        /**
         * @param isIndexed sign that form array is indexed
         * @param values    list values
         * @return new instance of {@link IChainList} with values
         */
        @SuppressWarnings("SameParameterValue")
        protected IChainList getNewIChainList(final boolean isIndexed, final Object... values) {
            final IChainList.Default list = new IChainList.Default(isIndexed);
            list.addAll(Arrays.asList(values));
            return list;
        }

        /**
         * Merge indexed {@link IChainList}
         * - Set by index - target null value is replaced by source value at the corresponding index
         * - Replace simple obj by index - target non-null value is replaced by source value at the corresponding index
         * - Merge complex obj by index - target value is merged by source value at the corresponding index
         *
         * @param source indexed {@link IChainList} for merging
         * @param target indexed {@link IChainList} for merging
         * @return merge result ({@link IChainList})
         * @throws MarshallerException if source or target is null
         * @throws ChainException      incompatible types
         */
        @SuppressWarnings({"java:S125", "java:S3776"})
        protected IChainList mergeIndexedIChainLists(final IChainList source, final IChainList target) {
            FormUrlUtils.parameterRequireNonNull(source, CodecConstant.SOURCE_PARAMETER);
            FormUrlUtils.parameterRequireNonNull(target, CodecConstant.TARGET_PARAMETER);
            boolean isReverse = source.size() > target.size();
            final IChainList longer = isReverse ? source : target;
            final IChainList shorter = isReverse ? target : source;
            if (longer.isNotFilled() || shorter.isNotFilled()) {
                // For an indexed list, inserting the smaller into the larger is important.
                // longer  [null, null, null, foo, null]
                //                ^^^
                // shorter [null, bar]
                final List<Integer> insertIndexes = new ArrayList<>();
                if (longer.size() >= shorter.size()) {
                    insertIndexes.addAll(shorter.stream()
                            .filter(Map.class::isInstance)
                            .map(shorter::indexOf)
                            .collect(Collectors.toList()));
                    for (Integer i : insertIndexes) { // insert map to list by index
                        if (longer.get(i) == null) {
                            longer.set(i, shorter.get(i));
                        } else {
                            final Map<String, Object> mergeResult = mergeRawMap(shorter.get(i), longer.get(i));
                            longer.set(i, mergeResult);
                        }
                    }
                }
                // Override not inserted objects by index
                // Example: foo[0]=bar&foo[0]=car -> foo=[car]
                shorter.stream()
                        .map(v -> v == null ? null : shorter.indexOf(v))
                        .filter(Objects::nonNull)
                        .filter(i -> !insertIndexes.contains(i))
                        .forEach(i -> longer.set(i, shorter.get(i)));
            } else {
                if (FormUrlUtils.isCollectionOfSimpleObj(longer) && FormUrlUtils.isCollectionOfSimpleObj(shorter)) {
                    // overwrite simple values by same index
                    // Example: foo[0]=bar&foo[0]=car -> foo=[car]
                    shorter.forEach(e -> longer.set(shorter.indexOf(e), e));
                } else {
                    // merge complex values by same index
                    // Example: foo[0][bar]=ccc&foo[0][car]=jjj -> {foo=[{bar=ccc, car=jjj}]}
                    for (Object sourceValue : shorter) {
                        final int i = shorter.indexOf(sourceValue);
                        final Object mergeResult = mergeObjectValues(sourceValue, longer.get(i));
                        longer.set(i, mergeResult);
                    }
                }
            }
            return longer;
        }

        /**
         * Merge non-indexed {@link IChainList}
         * For unindexed form list
         * - If list of maps - merge maps and set to target collection by corresponding index
         * - else appends all elements in the target collection
         *
         * @param source non-indexed {@link IChainList} for merging
         * @param target non-indexed {@link IChainList} for merging
         * @return merge result ({@link IChainList})
         */
        protected IChainList mergeNonIndexedIChainLists(final IChainList source, final IChainList target) {
            FormUrlUtils.parameterRequireNonNull(source, CodecConstant.SOURCE_PARAMETER);
            FormUrlUtils.parameterRequireNonNull(target, CodecConstant.TARGET_PARAMETER);
            if (FormUrlUtils.isMapIChainList(source)) {
                for (Object o : source) {
                    final int i = source.indexOf(o);
                    final Map<?, ?> sMap = (Map<?, ?>) source.get(i);
                    final Map<?, ?> tMap = (Map<?, ?>) target.get(i);
                    // unreliable heuristic for non-indexed arrays
                    if (sMap != null && tMap != null && !sMap.keySet().equals(tMap.keySet())) {
                        // merge source maps list with target maps list if maps keys is different
                        // merge [{foo=foo_val}] with [{bar=bar_val}]
                        // result [{foo=foo_val, bar=bar_val}]
                        target.set(i, mergeRawMap(sMap, tMap));
                    } else {
                        // append source maps list to target maps list if maps keys is same
                        // append [{foo=source}] to [{foo=target}]
                        // result [{foo=target}, {foo=source}]
                        target.addAll(source);
                    }
                }
            } else {
                target.addAll(source);
            }
            return target;
        }

        /**
         * Method for {@link IChainList} merging
         *
         * @param source {@link IChainList} for merging
         * @param target {@link IChainList} for merging
         * @return merge result ({@link IChainList})
         * @throws MarshallerException if source or target is null
         * @throws ChainException      incompatible types
         * @throws ChainException      different IChainList types (indexed/unindexed)
         */
        protected IChainList mergeIChainLists(final Object source, final Object target) {
            FormUrlUtils.parameterRequireNonNull(source, CodecConstant.SOURCE_PARAMETER);
            FormUrlUtils.parameterRequireNonNull(target, CodecConstant.TARGET_PARAMETER);
            if (!(source instanceof IChainList && target instanceof IChainList)) {
                throw new ChainException("Received incompatible types to merge\n" +
                                         "Expected type: " + IChainList.class + "\n" +
                                         "Actual source: " + source.getClass() + "\n" +
                                         "Actual target: " + target.getClass() + "\n");
            }
            final IChainList sourceList = (IChainList) source;
            final IChainList targetList = (IChainList) target;
            if (sourceList.isEmpty() || targetList.isEmpty()) {
                return sourceList.isEmpty() ? targetList : sourceList;
            }
            final boolean sNotIndexed = sourceList.isNotIndexed();
            final boolean tNotIndexed = targetList.isNotIndexed();
            if ((sNotIndexed && !tNotIndexed) || (!sNotIndexed && tNotIndexed)) {
                throw new ChainException("Different types of lists are passed for merging.\n" +
                                         "Source list: " + (sNotIndexed ? "not indexed" : "indexed") + "\n" +
                                         "Target list: " + (tNotIndexed ? "not indexed" : "indexed") + "\n");
            }
            return sNotIndexed ?
                    mergeNonIndexedIChainLists(sourceList, targetList) :
                    mergeIndexedIChainLists(sourceList, targetList);
        }

        /**
         * Transforms from url encoded parameters string to list of separated {@link IChainPart} (key/value pairs)
         *
         * @param urlEncodedString from url encoded parameters ({@code foo[bar][0]=value1&foo[bar][1]=value2})
         * @param codingCharset    URL form data coding charset
         * @return form data {@link IChainPart} where part contains one key/value pair ({@code foo[bar][0]=value1})
         * @throws ChainException key-value pair is not in URL form format
         */
        protected List<IChainPart> readUrlEncodedString(final String urlEncodedString, final Charset codingCharset) {
            if (urlEncodedString == null || urlEncodedString.trim().isEmpty()) {
                return new ArrayList<>();
            }
            final String[] pairs = urlEncodedString.split("&");
            List<IChainPart> result = new ArrayList<>();
            for (String pair : pairs) {
                final String[] split = pair.contains("=") ? pair.split("=") : new String[]{};
                if (split.length > 2 || split.length == 0) {
                    throw new ChainException("URL encoded key-value pair is not in URL format:\n" +
                                             "Pair: " + urlEncodedString);
                }
                final String rawValue = (split.length == 1) ? "" : split[1].trim();
                final String rawKey = split[0].trim();
                assertKeyBrackets(rawKey);
                final boolean implicitList = rawKey.contains("[]");
                final boolean explicitList = Arrays.stream(rawKey.split("\\["))
                        .map(p -> p.replace("]", ""))
                        .anyMatch(NumberUtils::isDigits);
                final String value = FormUrlUtils.decode(rawValue, codingCharset);
                result.add(new IChainPart.Default(rawKey, value, implicitList, explicitList));
            }
            return result;
        }

        /**
         * @param key url form parameter key
         * @throws MarshallerException      if key is null
         * @throws ChainException incorrect ratio of opening and closing brackets
         * @throws ChainException list nesting [[]]
         */
        protected void assertKeyBrackets(String key) {
            FormUrlUtils.parameterRequireNonNull(key, CodecConstant.KEY_PARAMETER);
            if (!isEvenBracketsRatio(key)) {
                throw new ChainException("The key contains an incorrect ratio of opening and closing brackets.\n" +
                                         "Invalid key: " + key + "\n");
            }
            if (hasNestedBrackets(key)) {
                throw new ChainException("Key nesting is not allowed.\n" +
                                         "Invalid key: " + key + "\n" +
                                         "Expected nested object format: filter[foo][bar]\n" +
                                         "Expected nested list format: filter[foo][0]\n");
            }
        }

        /**
         * @param key url form parameter key
         * @return true if the key contains nested lists
         * @throws MarshallerException if key is null
         */
        protected boolean hasNestedBrackets(String key) {
            FormUrlUtils.parameterRequireNonNull(key, CodecConstant.KEY_PARAMETER);
            return key.contains("]]") || key.contains("[[");
        }

        /**
         * @param key url form parameter key
         * @return true if the key contains the same number of opening and closing brackets
         * @throws MarshallerException if key is null
         */
        protected boolean isEvenBracketsRatio(String key) {
            FormUrlUtils.parameterRequireNonNull(key, CodecConstant.KEY_PARAMETER);
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
         * @param rawData      from url encoded parameters raw representation
         * @param implicitList key contains an implicit array (unindexed) {@code foo[bar][]=value}
         * @param explicitList key contains an explicit array (indexed) {@code foo[bar][0]=value}
         * @return list of {@link IChainPart} formed from the received raw data
         */
        protected List<IChainPart> readModel(final Map<String, Object> rawData,
                                             final boolean implicitList,
                                             final boolean explicitList) {
            FormUrlUtils.parameterRequireNonNull(rawData, CodecConstant.RAW_DATA_PARAMETER);
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
         * @param chainPart parent {@link IChainPart}
         * @param value     object to convert to {@link IChainPart} list
         * @return child {@link IChainPart} list
         * @throws MarshallerException - chainPart or value is null
         * @throws ChainException      - unsupported value type
         */
        protected List<IChainPart> valueObjectToChainParts(final IChainPart chainPart, Object value) {
            FormUrlUtils.parameterRequireNonNull(chainPart, CodecConstant.CHAIN_PART_PARAMETER);
            FormUrlUtils.parameterRequireNonNull(value, CodecConstant.VALUE_PARAMETER);
            final List<IChainPart> result = new ArrayList<>();
            if (FormUrlUtils.isSimple(value)) {
                result.add(simpleToChainPart(chainPart, value));
            } else if (FormUrlUtils.isCollection(value)) {
                final List<IChainPart> collectionChainParts = collectionToChainPart(chainPart, (Collection<?>) value);
                result.addAll(collectionChainParts);
            } else if (FormUrlUtils.isMapAssignableFrom(value)) {
                final List<IChainPart> mapChainParts = mapToChainPart(chainPart, (Map<?, ?>) value);
                result.addAll(mapChainParts);
            } else {
                throw ChainException.builder()
                        .errorMessage("Unsupported type for conversion.")
                        .actualType(value)
                        .actualValue(value)
                        .expected(ERR_SIMPLE_REFERENCE_TYPES)
                        .expectedHeirsOf(Map.class)
                        .expectedHeirsOf(Collection.class)
                        .build();
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
         * @param chainPart parent {@link IChainPart}
         * @param value     map to convert to {@link IChainPart} list
         * @return child {@link IChainPart} list
         * @throws MarshallerException - chainPart or value is null
         */
        protected List<IChainPart> mapToChainPart(IChainPart chainPart, Map<?, ?> value) {
            FormUrlUtils.parameterRequireNonNull(chainPart, "chainPart");
            FormUrlUtils.parameterRequireNonNull(value, CodecConstant.VALUE_PARAMETER);
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
         * @param chainPart parent {@link IChainPart}
         * @param value     value for chainPart
         * @return chainPart ({@link IChainPart})
         * @throws MarshallerException - chainPart or value is null
         */
        protected IChainPart simpleToChainPart(IChainPart chainPart, final Object value) {
            FormUrlUtils.parameterRequireNonNull(chainPart, CodecConstant.CHAIN_PART_PARAMETER);
            FormUrlUtils.parameterRequireNonNull(value, CodecConstant.VALUE_PARAMETER);
            return chainPart.setValue(String.valueOf(value));
        }

        /**
         * Method for converting a list to an IChainPart list based on the parent IChainPart
         * For example:
         * parent IChainPart = {@code foo=null}
         * value is a List object = {@code [value]} (nested)
         * the method will return a list with one IChainPart - {@code foo[0]=value}
         *
         * @param chainPart parent {@link IChainPart}
         * @param value     list to convert to {@link IChainPart} list
         * @return child {@link IChainPart} list
         * @throws MarshallerException - chainPart or value is null
         */
        protected List<IChainPart> collectionToChainPart(IChainPart chainPart, final Collection<?> value) {
            FormUrlUtils.parameterRequireNonNull(chainPart, CodecConstant.CHAIN_PART_PARAMETER);
            FormUrlUtils.parameterRequireNonNull(value, CodecConstant.VALUE_PARAMETER);
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
         * @param prettyPrint {@code prettyPrint ? "&\n" : "&"}
         * @return pretty printed URL form data (without value encoding/decoding)
         */
        public String toString(boolean prettyPrint) {
            StringJoiner sj = new StringJoiner(prettyPrint ? "&\n" : "&");
            getChainParts().forEach(e -> sj.add(e.toString()));
            return sj.toString();
        }
    }
}
