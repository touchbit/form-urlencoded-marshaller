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

package org.touchbit.www.form.urlencoded.marshaller;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.touchbit.www.form.urlencoded.marshaller.chain.IChain;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;
import org.touchbit.www.form.urlencoded.marshaller.util.CodecConstant;
import org.touchbit.www.form.urlencoded.marshaller.util.FormUrlUtils;
import org.touchbit.www.form.urlencoded.marshaller.util.MarshallerException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.touchbit.www.form.urlencoded.marshaller.util.CodecConstant.*;

/**
 * Convert model (JavaBean or {@code Map<String, ?>}) to URL encoded form and back to model.
 * Model example:
 * <pre><code>
 * &#64;FormUrlEncoded()
 * public class Model {
 *
 *     &#64;FormUrlEncodedField("foo")
 *     private String foo;
 *
 *     &#64;FormUrlEncodedField("bar")
 *     private List<Integer> bar;
 *
 *     &#64;FormUrlEncodedAdditionalProperties()
 *     private Map<String, Object> additionalProperties;
 *
 * }
 * </code></pre>
 * <p>
 * Usage:
 * <pre><code>
 *     Model model = new Model().foo("text").bar(1,2,3);
 *     Strung formUrlEncodedString = FormUrlMarshaller.INSTANCE.marshal(model);
 *     Model formUrlDecodedModel = FormUrlMarshaller.INSTANCE.unmarshal(formUrlEncodedString);
 * </code></pre>
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 19.02.2022
 * @see FormUrlEncoded
 * @see FormUrlEncodedField
 * @see FormUrlEncodedAdditionalProperties
 */
public class FormUrlMarshaller {

    /***/
    public static final FormUrlMarshaller INSTANCE = new FormUrlMarshaller();
    /***/
    private boolean prohibitAdditionalProperties = false;
    /***/
    private Charset codingCharset = StandardCharsets.UTF_8;
    /***/
    private boolean isImplicitList = false;
    /***/
    private boolean isExplicitList = false;
    /***/
    private NullValueRule nullValueRule = NullValueRule.RULE_IGNORE;

    /**
     * Converts a POJO or Map to a form URL encoded string
     *
     * @param model {@code Map<String, Object>} or pojo object with {@link FormUrlEncoded} annotation
     * @return form url encoded string
     * @throws MarshallerException for any internal errors.
     */
    public String marshal(final Object model) {
        final StringJoiner sj = new StringJoiner("&");
        marshalToMap(model).forEach((key, valueList) -> valueList.forEach(value -> sj.add(key + "=" + value)));
        return sj.toString();
    }

    /**
     * Converts a POJO or Map to a form URL encoded Map where
     * key - URL form Key,
     * value - list of encoded values.
     * For example: {foo=[1, 2], bar=car} <--> {foo=[1, 2], bar=[car]}.
     * Allows you to implement your own processing of form URL encoded lists.
     *
     * @param model {@code Map<String, Object>} or pojo object with {@link FormUrlEncoded} annotation
     * @return {@link Map}
     * @throws MarshallerException if model is null
     * @throws MarshallerException if model type is not supported
     */
    public Map<String, List<String>> marshalToMap(final Object model) {
        final Map<String, List<String>> result = new HashMap<>();
        marshalToIChain(model).getChainParts().forEach(part ->
                result.computeIfAbsent(part.getKey(), i -> new ArrayList<>()).add(part.getValue()));
        return result;
    }

    /**
     * Converts a POJO or Map to IChain object.
     * IChain - this is a chain of encoded url form parameters.
     * Allows you to implement your own processing of form URL encoded string data.
     *
     * @param model {@code Map<String, Object>} or pojo object with {@link FormUrlEncoded} annotation
     * @return {@link IChain}
     * @throws MarshallerException if model is null
     * @throws MarshallerException if model type is not supported
     */
    public IChain marshalToIChain(final Object model) {
        FormUrlUtils.parameterRequireNonNull(model, MODEL_PARAMETER);
        try {
            if (FormUrlUtils.isMapAssignableFrom(model) || FormUrlUtils.isPojo(model)) {
                //noinspection unchecked
                final Map<String, Object> rawData = (Map<String, Object>) convertValueToRawData(model);
                return new IChain.Default(rawData, isImplicitList(), isExplicitList());
            }
            throw MarshallerException.builder()
                    .errorMessage(ERR_RECEIVED_UNSUPPORTED_TYPE_FOR_CONVERSION)
                    .actualType(model)
                    .expectedHeirsOf(Map.class)
                    .expected(ERR_POJO_CLASSES_WITH_FORM_URLENCODED_ANNOTATION)
                    .build();
        } catch (MarshallerException e) {
            throw e;
        } catch (RuntimeException e) {
            throw MarshallerException.builder()
                    .errorMessage("Unexpected marshalling error.")
                    .errorCause(e)
                    .build();
        }
    }

    /**
     * String to model conversion
     *
     * @param modelClass    FormUrlEncoded model class
     * @param encodedString URL encoded string to conversation
     * @param <M>           model generic type
     * @return completed model
     * @throws MarshallerException for any internal errors.
     */
    public <M> M unmarshal(final Class<M> modelClass, final String encodedString) {
        try {
            return unmarshalStringToClass(modelClass, encodedString);
        } catch (MarshallerException e) {
            throw e;
        } catch (RuntimeException e) {
            throw MarshallerException.builder()
                    .errorMessage("Unexpected unmarshalling error.")
                    .errorCause(e)
                    .build();
        }
    }

    /**
     * String to model conversion
     *
     * @param object        POJO or Map object
     * @param encodedString URL encoded string to conversation
     * @param <M>           model generic type
     * @throws MarshallerException for any internal errors.
     */
    public <M> void unmarshalTo(final M object, final String encodedString) {
        try {
            unmarshalStringToObject(object, encodedString);
        } catch (MarshallerException e) {
            throw e;
        } catch (RuntimeException e) {
            throw MarshallerException.builder()
                    .errorMessage("Unexpected unmarshalling error.")
                    .errorCause(e)
                    .build();
        }
    }

    /**
     * String to model conversion
     *
     * @param modelClass    FormUrlEncoded model class
     * @param encodedString URL encoded string to conversation
     * @param <M>           model generic type
     * @return completed model
     * @throws MarshallerException on class instantiation errors
     */
    protected <M> M unmarshalStringToClass(final Class<M> modelClass, final String encodedString) {
        FormUrlUtils.parameterRequireNonNull(modelClass, MODEL_CLASS_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(encodedString, ENCODED_STRING_PARAMETER);
        final M model = FormUrlUtils.invokeConstructor(modelClass);
        unmarshalStringToObject(model, encodedString);
        return model;
    }

    /**
     * String to model conversion
     *
     * @param object        POJO or Map object
     * @param encodedString URL encoded string to conversation
     * @param <M>           model generic type
     * @throws MarshallerException on class instantiation errors
     */
    @SuppressWarnings("unchecked")
    protected <M> void unmarshalStringToObject(final M object, final String encodedString) {
        FormUrlUtils.parameterRequireNonNull(object, OBJECT_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(encodedString, ENCODED_STRING_PARAMETER);
        final Map<String, Object> rawData = new IChain.Default(encodedString).getRawData();
        if (FormUrlUtils.isMapAssignableFrom(object) || FormUrlUtils.isPojo(object)) {
            if (FormUrlUtils.isPojo(object)) {
                writeRawDataToPojo(object, rawData);
            }
            if (FormUrlUtils.isMapAssignableFrom(object)) {
                Map<String, Object> modelMap = (Map<String, Object>) object;
                modelMap.putAll(rawData);
                rawData.clear();
            }
            if (!rawData.isEmpty() && prohibitAdditionalProperties) {
                throw MarshallerException.builder()
                        .errorMessage(ERR_UNMAPPED_ADDITIONAL_PROPERTIES)
                        .actual(rawData)
                        .expected(THERE_ARE_NO_ADDITIONAL_PROPERTIES)
                        .build();
            }
            return;
        }
        throw MarshallerException.builder()
                .errorMessage(ERR_RECEIVED_UNSUPPORTED_TYPE_FOR_CONVERSION)
                .actualType(object)
                .expected(ERR_POJO_CLASSES_WITH_FORM_URLENCODED_ANNOTATION)
                .expectedHeirsOf(Map.class)
                .build();
    }

    /**
     * @param value any object
     * @return converted value (List || Map || String)
     * @throws MarshallerException if value type is not supported
     */
    protected Object convertValueToRawData(final Object value) {
        if (value == null || FormUrlUtils.isSimple(value)) {
            return convertSimpleToRawData(value);
        } else if (FormUrlUtils.isPojo(value) || FormUrlUtils.isMapAssignableFrom(value)) {
            final Map<String, Object> map = new HashMap<>();
            if (FormUrlUtils.isPojo(value)) {
                map.putAll(convertPojoToRawData(value));
            }
            if (FormUrlUtils.isMapAssignableFrom(value)) {
                map.putAll(convertMapToRawData(value));
            }
            return map;
        } else if (FormUrlUtils.isCollection(value)) {
            return convertCollectionToRawData(value);
        } else if (FormUrlUtils.isArray(value)) {
            return convertArrayToRawData(value);
        } else {
            throw MarshallerException.builder()
                    .errorMessage(ERR_RECEIVED_UNSUPPORTED_TYPE_FOR_CONVERSION)
                    .actualType(value)
                    .expected(ERR_SIMPLE_REFERENCE_TYPES)
                    .expected(ERR_POJO_CLASSES_WITH_FORM_URLENCODED_ANNOTATION)
                    .expectedHeirsOf(Map.class)
                    .expectedHeirsOf(Collection.class)
                    .expected(ERR_SIMPLE_COMPLEX_REFERENCE_TYPE_ARRAY)
                    .build();
        }
    }

    /**
     * @param value any object with {@link FormUrlEncoded} class annotation
     * @return {@link HashMap} with converted values
     * @throws MarshallerException if value is null
     * @throws MarshallerException class does not contain a FormUrlEncodedField annotation
     */
    @SuppressWarnings("java:S3776") // does not require decomposition
    protected Map<String, Object> convertPojoToRawData(final Object value) {
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        if (value.getClass().isAnnotationPresent(FormUrlEncoded.class)) {
            final Map<String, Object> result = new HashMap<>();
            final List<Field> formUrlEncodedFields = FormUrlUtils.getFormUrlEncodedFields(value);
            for (Field field : formUrlEncodedFields) {
                final FormUrlEncodedField annotation = field.getAnnotation(FormUrlEncodedField.class);
                final Object rawFieldValue = FormUrlUtils.readField(value, field);
                final Object fieldValue;
                if (rawFieldValue != null) {
                    fieldValue = rawFieldValue;
                } else {
                    switch (getNullValueRule()) {
                        case RULE_NULL_MARKER:
                            if (annotation.encoded()) {
                                fieldValue = NullValueRule.ENCODED_NULL_MARKER;
                            } else {
                                fieldValue = NullValueRule.DECODED_NULL_MARKER;
                            }
                            break;
                        case RULE_NULL_STRING:
                            fieldValue = "null";
                            break;
                        case RULE_EMPTY_STRING:
                            fieldValue = "";
                            break;
                        case RULE_IGNORE:
                        default:
                            fieldValue = null;
                    }
                }
                if (fieldValue == null) {
                    continue;
                }
                final String resultKey = annotation.value();
                final Object resultValue;
                if (FormUrlUtils.isSimple(fieldValue)) {
                    final String stringValue = String.valueOf(fieldValue);
                    final String encoded = FormUrlUtils.encode(stringValue, codingCharset);
                    resultValue = annotation.encoded() ? fieldValue : encoded;
                } else {
                    resultValue = convertValueToRawData(fieldValue);
                }
                result.put(resultKey, resultValue);
            }
            final Field additionalPropertiesField = getAdditionalPropertiesField(value.getClass());
            if (additionalPropertiesField != null) {
                final Map<Object, Object> ap = getAdditionalProperties(value, additionalPropertiesField);
                @SuppressWarnings("unchecked") final Map<String, Object> convertedAP = (Map<String, Object>) convertValueToRawData(ap);
                result.putAll(convertedAP);
            }
            return result;
        }
        throw MarshallerException.builder()
                .errorMessage("Class does not contain a required annotation.")
                .source(value.getClass())
                .expected("@" + FormUrlEncoded.class.getSimpleName())
                .build();
    }

    /**
     * @param value any {@link Map}
     * @return {@link HashMap} with converted values
     * @throws MarshallerException if value is null
     * @throws MarshallerException if value is not {@link Map}
     * @throws MarshallerException if map keys is not {@link String}
     */
    protected Map<String, Object> convertMapToRawData(final Object value) {
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        final Map<String, Object> result = new HashMap<>();
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            final Set<?> rawKeySet = map.keySet();
            final List<?> invalidTypeKeys = rawKeySet.stream()
                    .filter(e -> !(e instanceof String))
                    .collect(Collectors.toList());
            if (!invalidTypeKeys.isEmpty()) {
                final Map<?, String> actual = invalidTypeKeys.stream()
                        .collect(Collectors.toMap(i -> i, i -> i.getClass().getSimpleName()));
                throw MarshallerException.builder()
                        .errorMessage("Invalid Map keys type")
                        .actual("key-type pairs: " + actual)
                        .expected("String key type (Map<String, ?>)")
                        .build();
            }
            for (Object rawKey : rawKeySet) {
                final String resultKey = String.valueOf(rawKey);
                final Object resultValue = map.get(resultKey);
                final Object rawDataValue = convertValueToRawData(resultValue);
                result.put(resultKey, rawDataValue);
            }
            return result;
        }
        throw MarshallerException.builder()
                .errorMessage(ERR_RECEIVED_UNSUPPORTED_TYPE_FOR_CONVERSION)
                .actualType(value)
                .expectedHeirsOf(Map.class)
                .build();
    }

    /**
     * @param value any {@link Collection}
     * @return {@link ArrayList} with converted array values
     * @throws MarshallerException if value is null
     * @throws MarshallerException if value is not {@link Collection}
     */
    protected Object convertCollectionToRawData(final Object value) {
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        final List<Object> result = new ArrayList<>();
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            collection.forEach(item -> result.add(convertValueToRawData(item)));
            return result;
        }
        throw MarshallerException.builder()
                .errorMessage(ERR_RECEIVED_UNSUPPORTED_TYPE_FOR_CONVERSION)
                .actualType(value)
                .expectedHeirsOf(Collection.class)
                .build();
    }

    /**
     * @param value any array
     * @return {@link ArrayList} with converted array values
     * @throws MarshallerException if value is null
     * @throws MarshallerException if value is not array
     */
    protected Object convertArrayToRawData(final Object value) {
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        final List<Object> result = new ArrayList<>();
        if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            Arrays.stream(array).forEach(item -> result.add(convertValueToRawData(item)));
            return result;
        }
        throw MarshallerException.builder()
                .errorMessage(ERR_RECEIVED_UNSUPPORTED_TYPE_FOR_CONVERSION)
                .actualType(value)
                .expected(ERR_SIMPLE_COMPLEX_REFERENCE_TYPE_ARRAY)
                .build();
    }

    /**
     * @param value simple value (String, Integer, Double, etc.)
     * @return encoded value or empty string if value is null
     */
    protected Object convertSimpleToRawData(final Object value) {
        return FormUrlUtils.encode(value == null ? "" : String.valueOf(value), codingCharset);
    }

    /**
     * The method casts raw data to POJO field types and
     * writes the converted values to the corresponding POJO fields.
     *
     * @param model   POJO object
     * @param rawData data to write to the model
     * @param <M>     model generic type
     */
    @SuppressWarnings("java:S3776") // does not require decomposition
    protected <M> void writeRawDataToPojo(final M model, final Map<?, ?> rawData) {
        FormUrlUtils.parameterRequireNonNull(model, MODEL_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(rawData, RAW_DATA_PARAMETER);
        FormUrlUtils.getFormUrlEncodedFieldsMap(model)
                .forEach((urlEncodedFieldName, pojoField) -> {
                    if (rawData.get(urlEncodedFieldName) == null) {
                        return;
                    }
                    final Object rawDataValue = rawData.get(urlEncodedFieldName);
                    final Type fieldType = pojoField.getGenericType();
                    if (!FormUrlUtils.isSomePojo(fieldType)) {
                        final Object fieldValue = convertRawValueToTargetJavaType(rawDataValue, fieldType);
                        FormUrlUtils.writeDeclaredField(model, pojoField, fieldValue);
                        rawData.remove(urlEncodedFieldName);
                    } else {
                        if (!FormUrlUtils.isMapAssignableFrom(rawDataValue)) {
                            throw MarshallerException.builder()
                                    .errorMessage(ERR_INCOMPATIBLE_TYPES_RECEIVED_FOR_CONVERSION)
                                    .source(rawData)
                                    .sourceField(urlEncodedFieldName)
                                    .sourceValue(rawDataValue)
                                    .sourceType(rawDataValue)
                                    .targetType(fieldType)
                                    .targetField(pojoField)
                                    .build();
                        } else {
                            if (FormUrlUtils.isPojo(fieldType)) {
                                // raw value Map<String, Object> to target Pojo
                                final Class<?> pojoClass = TypeUtils.getRawType(fieldType, null);
                                final Object pojo = FormUrlUtils.invokeConstructor(pojoClass);
                                writeRawDataToPojo(pojo, (Map<?, ?>) rawDataValue);
                                FormUrlUtils.writeDeclaredField(model, pojoField, pojo);
                                rawData.remove(urlEncodedFieldName);
                            } else if (FormUrlUtils.isPojoGenericCollection(fieldType)) {
                                // raw value Map<String, Object>  to target List<Pojo> (hidden url encoded array)
                                final Class<?> pojoClass = FormUrlUtils.getGenericCollectionArgumentRawType(fieldType);
                                final Object pojo = FormUrlUtils.invokeConstructor(pojoClass);
                                writeRawDataToPojo(pojo, (Map<?, ?>) rawDataValue);
                                FormUrlUtils.writeDeclaredField(model, pojoField, Collections.singletonList(pojo));
                                rawData.remove(urlEncodedFieldName);
                            } else if (FormUrlUtils.isPojoArray(fieldType)) {
                                // raw value Map<String, Object>  to target Pojo[] (hidden url encoded array)
                                final Class<?> arrayComponentClass = FormUrlUtils.getArrayComponentClass(fieldType);
                                final Object pojo = FormUrlUtils.invokeConstructor(arrayComponentClass);
                                writeRawDataToPojo(pojo, (Map<?, ?>) rawDataValue);
                                final Object[] value = FormUrlUtils.objectToArray(pojo, arrayComponentClass);
                                FormUrlUtils.writeDeclaredField(model, pojoField, value);
                                rawData.remove(urlEncodedFieldName);
                            }
                        }
                    }
                });
        if (FormUrlUtils.hasAdditionalProperty(model) && !rawData.isEmpty()) {
            if (!prohibitAdditionalProperties) {
                unmarshalAndWriteAdditionalProperties(model, rawData);
                rawData.clear();
            } else {
                throw MarshallerException.builder()
                        .errorMessage(ERR_UNMAPPED_ADDITIONAL_PROPERTIES)
                        .actual(rawData)
                        .expected(THERE_ARE_NO_ADDITIONAL_PROPERTIES)
                        .build();
            }
        }
    }

    /**
     * The method converts the raw value to the desired type
     *
     * @param rawValue   any object
     * @param targetType target Type for conversion
     * @return converted value
     * @throws MarshallerException if rawValue and targetType is not compatible
     */
    @SuppressWarnings("java:S3776") // does not require decomposition
    protected Object convertRawValueToTargetJavaType(final Object rawValue, Type targetType) {
        if (FormUrlUtils.isSimple(rawValue) && FormUrlUtils.isSimple(targetType)) {
            // raw value String to target type Integer
            return convertUrlDecodedStringValueToSimpleType(String.valueOf(rawValue), targetType);
        }
        if (FormUrlUtils.isSimple(rawValue) && FormUrlUtils.isGenericCollection(targetType)) {
            // raw value String to target type List<Integer> (hidden url encoded array)
            final ParameterizedType parameterizedTargetType = (ParameterizedType) targetType;
            final Type targetGenericType = parameterizedTargetType.getActualTypeArguments()[0];
            final Object value = convertUrlDecodedStringValueToSimpleType(String.valueOf(rawValue), targetGenericType);
            return Collections.singletonList(value);
        }
        if (FormUrlUtils.isSimple(rawValue) && FormUrlUtils.isArray(targetType)) {
            // raw value String to target type Integer[] (hidden url encoded array)
            final Class<?> componentType = FormUrlUtils.getArrayComponentClass(targetType);
            final Object value = convertUrlDecodedStringValueToSimpleType(String.valueOf(rawValue), componentType);
            return FormUrlUtils.objectToArray(value, componentType);
        }
        if (FormUrlUtils.isMapAssignableFrom(rawValue) && FormUrlUtils.isGenericMap(targetType)) {
            final Map<?, ?> rawMap = (Map<?, ?>) rawValue;
            final ParameterizedType parameterizedTargetType = (ParameterizedType) targetType;
            final Type targetGenericValueType = parameterizedTargetType.getActualTypeArguments()[1];
            if (targetGenericValueType.equals(Object.class)) {
                // Target type Map<String, Object>. No need for conversion.
                return rawValue;
            }
            // raw value Map<String, Object> to target type Map<String, ?>
            final List<String> keys = rawMap.keySet().stream().map(Object::toString).collect(Collectors.toList());
            final Map<String, Object> result = new HashMap<>();
            for (String key : keys) {
                final Object rawMapValue = rawMap.get(key);
                final Object resultValue = convertRawValueToTargetJavaType(rawMapValue, targetGenericValueType);
                result.put(key, resultValue);
            }
            return result;
        }
        if (FormUrlUtils.isMapAssignableFrom(rawValue) && FormUrlUtils.isMapAssignableFrom(targetType)) {
            // raw value Map<String, Object> to target type raw Map (raw map without generic)
            return rawValue;
        }

        if (FormUrlUtils.isCollection(rawValue) && FormUrlUtils.isArray(targetType)) {
            // raw value List<String> to target type Integer[]
            final Collection<?> rawCollection = (Collection<?>) rawValue;
            final Class<?> componentType = FormUrlUtils.getArrayComponentClass(targetType);
            final List<Object> value = rawCollection.stream()
                    .map(i -> (i == null ? null : String.valueOf(i)))
                    .map(i -> convertUrlDecodedStringValueToSimpleType(i, componentType))
                    .collect(Collectors.toList());
            return FormUrlUtils.collectionToArray(value, componentType);
        }
        if (FormUrlUtils.isCollection(rawValue) && FormUrlUtils.isGenericCollection(targetType)) {
            final Collection<?> rawCollection = (Collection<?>) rawValue;
            final ParameterizedType parameterizedTargetType = (ParameterizedType) targetType;
            final Type targetGenericType = parameterizedTargetType.getActualTypeArguments()[0];
            if (FormUrlUtils.isCollectionOfMap(rawValue) && FormUrlUtils.isCollectionOfMap(targetType)) {
                // raw value List<Map<?, ?>> to target type List<Map<?, ?>>
                return rawCollection.stream()
                        .map(i -> convertRawValueToTargetJavaType(i, targetGenericType))
                        .collect(Collectors.toList());
            }
            if (targetGenericType.equals(Object.class)) {
                // raw value List<String> to target type List<Object>
                return rawValue;
            }
            if (FormUrlUtils.isCollectionOfCollections(rawValue) && FormUrlUtils.isCollectionOfCollections(targetType)) {
                // raw value List<List<String>> to target type List<List<Integer>>
                return rawCollection.stream()
                        .map(i -> convertRawValueToTargetJavaType(i, targetGenericType))
                        .collect(Collectors.toList());
            }
            if (FormUrlUtils.isCollectionOfCollections(rawValue) && FormUrlUtils.isCollectionOfArray(targetType)) {
                // raw value List<List<String>> to target type List<Integer[]>
                final ParameterizedType parameterizedType = (ParameterizedType) targetType;
                final Class<?> genericClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                return rawCollection.stream()
                        .map(i -> convertRawValueToTargetJavaType(i, genericClass))
                        .collect(Collectors.toList());
            }
            if (FormUrlUtils.isCollectionOfSimpleObj(rawValue) && FormUrlUtils.isCollectionOfSimpleObj(targetType)) {
                // raw value List<String> to target type List<Integer>
                return rawCollection.stream()
                        .map(String::valueOf)
                        .map(i -> convertUrlDecodedStringValueToSimpleType(i, targetGenericType))
                        .collect(Collectors.toList());
            }
        }
        if (FormUrlUtils.isMapAssignableFrom(rawValue) && FormUrlUtils.isGenericCollection(targetType)) {
            // raw value Map<String, Object> to target type List<Map<?, ?>> (hidden url encoded array)
            final ParameterizedType parameterizedTargetType = (ParameterizedType) targetType;
            final Type targetGenericType = parameterizedTargetType.getActualTypeArguments()[0];
            final Object value = convertRawValueToTargetJavaType(rawValue, targetGenericType);
            return Collections.singletonList(value);
        }
        if (FormUrlUtils.isMapAssignableFrom(rawValue) && FormUrlUtils.isArray(targetType)) {
            // raw value Map<String, ?> to target type raw Map[] (hidden url encoded array)
            final Class<?> componentType = FormUrlUtils.getArrayComponentClass(targetType);
            final Object value = convertRawValueToTargetJavaType(rawValue, componentType);
            return FormUrlUtils.objectToArray(value, componentType);
        }
        if (FormUrlUtils.isCollection(rawValue) && FormUrlUtils.isCollection(targetType)) {
            // raw value List<Object> to target type List (raw generic list)
            return rawValue;
        }
        if (FormUrlUtils.isGenericArray(targetType)) {
            final Class<?> targetArrayComponentClass = FormUrlUtils.getArrayComponentClass(targetType);
            if (FormUrlUtils.isCollectionOfMap(rawValue)) {
                // raw value List<Map<String, ?>> to target type Map<String, ?>[]
                final Collection<?> rawCollection = (Collection<?>) rawValue;
                return rawCollection.stream()
                        .map(i -> convertRawValueToTargetJavaType(i, targetArrayComponentClass))
                        .collect(Collectors.toList())
                        .toArray((Object[]) Array.newInstance(targetArrayComponentClass, 0));
            }
            if (FormUrlUtils.isMapAssignableFrom(rawValue)) {
                // raw value Map<String, ?> to target type Map<String, ?>[] (hidden url encoded array)
                final Object value = convertRawValueToTargetJavaType(rawValue, targetArrayComponentClass);
                return FormUrlUtils.objectToArray(value, targetArrayComponentClass);
            }
        }
        throw MarshallerException.builder()
                .errorMessage(ERR_INCOMPATIBLE_TYPES_RECEIVED_FOR_CONVERSION)
                .sourceValue(rawValue)
                .sourceType(rawValue)
                .targetType(targetType)
                .build();
    }

    /**
     * Populates the field marked with the {@link FormUrlEncodedField} annotation
     * with the received data that is not in the model.
     *
     * @param model   FormUrlEncoded model
     * @param rawData unhandled data ({@code Map<String, Object>} by default)
     * @param <M>     model generic type
     * @throws MarshallerException if unable to initialize additionalProperties field
     */

    protected <M> void unmarshalAndWriteAdditionalProperties(final M model,
                                                             final Map<?, ?> rawData) {
        FormUrlUtils.parameterRequireNonNull(model, MODEL_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(rawData, RAW_DATA_PARAMETER);
        final Field field = getAdditionalPropertiesField(model.getClass());
        final Map<Object, Object> additionalProperties = getAdditionalProperties(model, field);
        additionalProperties.putAll(rawData);
    }

    /**
     * @param modelClass FormUrlEncoded model class
     * @return field annotated with FormUrlEncodedAdditionalProperties or null
     * @throws MarshallerException if modelClass parameter is null
     * @throws MarshallerException if additionalProperties fields more than one
     * @throws MarshallerException if additionalProperties type != {@code Map<String, Object>}
     */
    protected Field getAdditionalPropertiesField(final Class<?> modelClass) {
        FormUrlUtils.parameterRequireNonNull(modelClass, CodecConstant.MODEL_CLASS_PARAMETER);
        final List<Field> fields = Arrays.asList(modelClass.getDeclaredFields());
        final List<Field> additionalProperties = fields.stream()
                .filter(f -> f.isAnnotationPresent(FormUrlEncodedAdditionalProperties.class))
                .collect(Collectors.toList());
        if (additionalProperties.size() > 1) {
            throw MarshallerException.builder()
                    .errorMessage("POJO contains more than one annotated fields.")
                    .model(modelClass)
                    .annotation(FormUrlEncodedAdditionalProperties.class)
                    .annotationType(FormUrlEncodedAdditionalProperties.class)
                    .fields(additionalProperties)
                    .expected("one annotated field")
                    .build();
        }
        if (additionalProperties.isEmpty()) {
            return null;
        }
        final Field additionalProperty = additionalProperties.get(0);
        final Type type = additionalProperty.getGenericType();
        final boolean isParameterizedType = type instanceof ParameterizedType;
        final boolean isMap = Map.class.isAssignableFrom(additionalProperty.getType());
        final boolean isValidTypeArguments;
        if (isParameterizedType && isMap) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            final Type keyType = actualTypeArguments[0];
            final Type valueType = actualTypeArguments[1];
            isValidTypeArguments = keyType == String.class && valueType == Object.class;
        } else {
            isValidTypeArguments = false;
        }
        if (!isParameterizedType || !isMap || !isValidTypeArguments) {
            throw MarshallerException.builder()
                    .errorMessage("Invalid additional properties field type")
                    .model(modelClass)
                    .field(additionalProperty)
                    .actualType(type)
                    .expected("java.util.Map<java.lang.String, java.lang.Object>")
                    .build();
        }
        return additionalProperty;
    }

    /**
     * If a field marked with the {@link FormUrlEncodedAdditionalProperties} annotation is present and not initialized,
     * then a new HashMap instance will be written to the field value.
     *
     * @param model FormUrlEncoded model
     * @param field model field with {@link FormUrlEncodedAdditionalProperties} annotation
     * @return additionalProperty field value (Map) or null if field not present
     * @throws MarshallerException if model parameter is null
     * @throws MarshallerException if unable to initialize additionalProperties field
     * @throws MarshallerException if additionalProperty field not readable
     */

    @SuppressWarnings("unchecked")
    protected Map<Object, Object> getAdditionalProperties(final Object model, final Field field) {
        FormUrlUtils.parameterRequireNonNull(model, MODEL_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final Map<Object, Object> fieldValue = (Map<Object, Object>) FormUrlUtils.readField(model, field);
        if (fieldValue == null) {
            final HashMap<Object, Object> value = new HashMap<>();
            FormUrlUtils.writeDeclaredField(model, field, value);
            return value;
        }
        return fieldValue;
    }

    /**
     * Converts a string to the target type.
     * Supports the following types for conversion:
     * - {@link String}
     * - {@link Object} (string by default)
     * - {@link Boolean}
     * - {@link Short}
     * - {@link Long}
     * - {@link Float}
     * - {@link Integer}
     * - {@link Double}
     * - {@link BigInteger}
     * - {@link BigDecimal}
     *
     * @param value      String value to convert
     * @param targetType Java type to which the string is converted
     * @return converted value
     * @throws MarshallerException   if targetType is primitive
     * @throws MarshallerException   if targetType not supported
     * @throws MarshallerException   if the value cannot be converted to {@link Boolean} type
     * @throws NumberFormatException if the value cannot be converted to number types
     */
    @SuppressWarnings("java:S3776")
    protected Object convertUrlDecodedStringValueToSimpleType(final String value, final Type targetType) {
        FormUrlUtils.parameterRequireNonNull(targetType, TARGET_TYPE_PARAMETER);
        if (targetType instanceof Class && ((Class<?>) targetType).isPrimitive()) {
            throw MarshallerException.builder()
                    .errorMessage("Forbidden to use primitive types")
                    .actualType(targetType)
                    .expected(ERR_SIMPLE_REFERENCE_TYPES)
                    .build();
        }
        if (value == null) {
            return null;
        }
        if (targetType.equals(String.class) || targetType.equals(Object.class)) {
            return value;
        }
        if (targetType.equals(Boolean.class)) {
            if ("true".equalsIgnoreCase(value)) {
                return true;
            }
            if ("false".equalsIgnoreCase(value)) {
                return false;
            }
            throw MarshallerException.builder()
                    .errorMessage(ERR_INCOMPATIBLE_TYPES_RECEIVED_FOR_CONVERSION)
                    .sourceType(value)
                    .sourceValue(value)
                    .targetType(targetType)
                    .expectedValue("true || false")
                    .build();
        }
        try {
            if (targetType.equals(Short.class)) {
                return Short.valueOf(value);
            }
            if (targetType.equals(Long.class)) {
                return Long.valueOf(value);
            }
            if (targetType.equals(Float.class)) {
                return Float.valueOf(value);
            }
            if (targetType.equals(Integer.class)) {
                return Integer.valueOf(value);
            }
            if (targetType.equals(Double.class)) {
                return Double.valueOf(value);
            }
            if (targetType.equals(BigInteger.class)) {
                return NumberUtils.createBigInteger(value);
            }
            if (targetType.equals(BigDecimal.class)) {
                return NumberUtils.createBigDecimal(value);
            }
        } catch (NumberFormatException e) {
            throw MarshallerException.builder()
                    .errorMessage(ERR_INCOMPATIBLE_TYPES_RECEIVED_FOR_CONVERSION)
                    .sourceType(value)
                    .sourceValue(value)
                    .targetType(targetType)
                    .errorCause(e)
                    .build();
        }
        throw MarshallerException.builder()
                .errorMessage(ERR_RECEIVED_UNSUPPORTED_TYPE_FOR_CONVERSION)
                .actualType(targetType)
                .expected(ERR_SIMPLE_REFERENCE_TYPES)
                .build();
    }

    /**
     * @param prohibitAdditionalProperties true - prohibit additional properties for POJO
     * @return this
     */
    public FormUrlMarshaller prohibitAdditionalProperties(boolean prohibitAdditionalProperties) {
        this.prohibitAdditionalProperties = prohibitAdditionalProperties;
        return this;
    }

    /**
     * @return true - prohibit additional properties for POJO
     */
    public boolean isProhibitAdditionalProperties() {
        return prohibitAdditionalProperties;
    }

    /**
     * @return URL form data coding charset
     */
    public Charset getFormUrlCodingCharset() {
        return codingCharset;
    }

    /**
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @param codingCharset URL form data coding charset
     * @return this
     */
    public FormUrlMarshaller setFormUrlCodingCharset(Charset codingCharset) {
        this.codingCharset = codingCharset;
        return this;
    }

    /**
     * Enable hidden array format: {@code foo=100&foo=200...&foo=100500}
     *
     * @return this
     */
    public FormUrlMarshaller enableHiddenList() {
        this.isImplicitList = false;
        this.isExplicitList = false;
        return this;
    }

    /**
     * Enable non-indexed array format: {@code foo[]=100&foo[]=200...&foo[]=100500}
     *
     * @return this
     */
    public FormUrlMarshaller enableImplicitList() {
        this.isImplicitList = true;
        this.isExplicitList = false;
        return this;
    }

    /**
     * Enable indexed array format: {@code foo[0]=100&foo[1]=200...&foo[n]=100500}
     *
     * @return this
     */
    public FormUrlMarshaller enableExplicitList() {
        this.isImplicitList = false;
        this.isExplicitList = true;
        return this;
    }

    /**
     * @return true if non-indexed array format enabled: {@code foo[]=100&foo[]=200...&foo[]=100500}
     */
    public boolean isImplicitList() {
        return isImplicitList;
    }

    /**
     * @return true if indexed array format enabled: {@code foo[0]=100&foo[1]=200...&foo[n]=100500}
     */
    public boolean isExplicitList() {
        return isExplicitList;
    }

    /**
     * @return true if hidden array format enabled: {@code foo=100&foo=200...&foo=100500}
     */
    public boolean isHiddenList() {
        return !isImplicitList() && !isExplicitList();
    }

    /**
     * @return {@link NullValueRule}
     * @see NullValueRule
     */
    public NullValueRule getNullValueRule() {
        return nullValueRule;
    }

    /**
     * @param nullValueRule - rule for handling fields with null value
     * @return this
     * @see NullValueRule
     */
    public FormUrlMarshaller setNullValueRule(final NullValueRule nullValueRule) {
        this.nullValueRule = nullValueRule;
        return this;
    }

}
