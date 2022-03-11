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

import java.lang.reflect.*;
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
public class FormUrlMarshaller implements IFormUrlMarshaller {

    public static final FormUrlMarshaller INSTANCE = new FormUrlMarshaller();
    private final IFormUrlMarshallerConfiguration configuration;

    private boolean prohibitAdditionalProperties = false;
    private Charset codingCharset = StandardCharsets.UTF_8;

    public FormUrlMarshaller() {
        this(new IFormUrlMarshallerConfiguration.Default().enableHiddenList());
    }

    public FormUrlMarshaller(final IFormUrlMarshallerConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * @return current marshaller configuration
     */
    @Override
    public IFormUrlMarshallerConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Model to string conversion
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @param model {@code Map<String, Object>} or pojo object with {@link FormUrlEncoded} annotation
     * @return form url encoded string
     * @throws NullPointerException if model is null
     * @throws MarshallerException  if model type is not supported
     */
    @Override
    public String marshal(final Object model) {
        FormUrlUtils.parameterRequireNonNull(model, MODEL_PARAMETER);
        if (FormUrlUtils.isMap(model) || FormUrlUtils.isPojo(model)) {
            //noinspection unchecked
            final Map<String, Object> rawData = new HashMap<>((Map<String, Object>) convertValueToRawData(model));
            final IChain chain = new IChain.Default(rawData, getConfiguration());
            final StringJoiner sj = new StringJoiner(getConfiguration().isPrettyPrint() ? "&\n" : "&");
            chain.getChainParts().forEach(e -> sj.add(e.getKey() + "=" + ((e.getValue() == null) ? "" : e.getValue())));
            return sj.toString();
        }
        throw MarshallerException.builder()
                .errorMessage(ERR_RECEIVED_UNSUPPORTED_TYPE_FOR_CONVERSION)
                .actualType(model)
                .expectedHeirsOf(Map.class)
                .expected(ERR_POJO_CLASSES_WITH_FORM_URLENCODED_ANNOTATION)
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
        } else if (FormUrlUtils.isPojo(value) || FormUrlUtils.isMap(value)) {
            final Map<String, Object> map = new HashMap<>();
            if (FormUrlUtils.isPojo(value)) {
                map.putAll(convertPojoToRawData(value));
            }
            if (FormUrlUtils.isMap(value)) {
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
     * @throws NullPointerException if value is null
     * @throws MarshallerException  class does not contain a FormUrlEncodedField annotation
     */
    protected Map<String, Object> convertPojoToRawData(final Object value) {
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        if (value.getClass().isAnnotationPresent(FormUrlEncoded.class)) {
            final Map<String, Object> result = new HashMap<>();
            final List<Field> formUrlEncodedFields = FormUrlUtils.getFormUrlEncodedFields(value);
            for (Field field : formUrlEncodedFields) {
                final FormUrlEncodedField annotation = field.getAnnotation(FormUrlEncodedField.class);
                final Object fieldValue = FormUrlUtils.readField(value, field);
                final String resultKey = annotation.value();
                final Object resultValue;
                if (fieldValue == null) {
                    continue;
                }
                if (FormUrlUtils.isSimple(fieldValue)) {
                    final String stringValue = String.valueOf(fieldValue);
                    final String encoded = FormUrlUtils.encode(stringValue, codingCharset);
                    resultValue = annotation.encoded() ? fieldValue : encoded;
                } else {
                    resultValue = convertValueToRawData(fieldValue);
                }
                result.put(resultKey, resultValue);
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
     * @throws NullPointerException if value is null
     * @throws MarshallerException  if value is not {@link Map}
     * @throws MarshallerException  if map keys is not {@link String}
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
     * @throws NullPointerException if value is null
     * @throws MarshallerException  if value is not {@link Collection}
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
     * @throws NullPointerException if value is null
     * @throws MarshallerException  if value is not array
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
     * String to model conversion
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @param modelClass    FormUrlEncoded model class
     * @param encodedString URL encoded string to conversation
     * @param <M>           model generic type
     * @return completed model
     * @throws MarshallerException on class instantiation errors
     */
    @Override
    public <M> M unmarshal(final Class<M> modelClass, final String encodedString) {
        FormUrlUtils.parameterRequireNonNull(modelClass, MODEL_CLASS_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(encodedString, ENCODED_STRING_PARAMETER);
        final M model = FormUrlUtils.invokeConstructor(modelClass);
        unmarshalTo(model, encodedString);
        return model;
    }

    /**
     * String to model conversion
     *
     * @param model         FormUrlEncoded model object
     * @param encodedString URL encoded string to conversation
     * @param <M>           model generic type
     * @throws MarshallerException on class instantiation errors
     */
    @SuppressWarnings("unchecked")
    public <M> void unmarshalTo(final M model, final String encodedString) {
        FormUrlUtils.parameterRequireNonNull(model, MODEL_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(encodedString, ENCODED_STRING_PARAMETER);
        final Map<String, Object> rawData = new IChain.Default(encodedString).getRawData();
        if (FormUrlUtils.isMap(model) || FormUrlUtils.isPojo(model)) {
            if (FormUrlUtils.isPojo(model)) {
                writeRawDataToPojo(model, rawData);
            }
            if (FormUrlUtils.isMap(model)) {
                Map<String, Object> modelMap = (Map<String, Object>) model;
                modelMap.putAll(rawData);
                rawData.clear();
            }
            if (!rawData.isEmpty() && isProhibitAdditionalProperties()) {
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
                .actualType(model)
                .expected(ERR_POJO_CLASSES_WITH_FORM_URLENCODED_ANNOTATION)
                .expectedHeirsOf(Map.class)
                .build();
    }

    @SuppressWarnings("java:S3776") // does not require decomposition
    protected <M> void writeRawDataToPojo(final M model, final Map<?, ?> rawData) {
        FormUrlUtils.parameterRequireNonNull(model, MODEL_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(rawData, RAW_DATA_PARAMETER);
        FormUrlUtils.getFormUrlEncodedFieldsMap(model)
                .entrySet().stream().filter(e -> rawData.get(e.getKey()) != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .forEach((urlEncodedFieldName, pojoField) -> {
                    final Object rawDataValue = rawData.get(urlEncodedFieldName);
                    final Type fieldType = pojoField.getGenericType();
                    if (!FormUrlUtils.isSomePojo(fieldType)) {
                        final Object fieldValue = convertRawValueToTargetJavaType(rawDataValue, fieldType);
                        FormUrlUtils.writeDeclaredField(model, pojoField, fieldValue);
                        rawData.remove(urlEncodedFieldName);
                    } else {
                        if (!FormUrlUtils.isMap(rawDataValue)) {
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
            if (!isProhibitAdditionalProperties()) {
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
        if (FormUrlUtils.isMap(rawValue) && FormUrlUtils.isGenericMap(targetType)) {
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
        if (FormUrlUtils.isMap(rawValue) && FormUrlUtils.isMap(targetType)) {
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
        if (FormUrlUtils.isMap(rawValue) && FormUrlUtils.isGenericCollection(targetType)) {
            // raw value Map<String, Object> to target type List<Map<?, ?>> (hidden url encoded array)
            final ParameterizedType parameterizedTargetType = (ParameterizedType) targetType;
            final Type targetGenericType = parameterizedTargetType.getActualTypeArguments()[0];
            final Object value = convertRawValueToTargetJavaType(rawValue, targetGenericType);
            return Collections.singletonList(value);
        }
        if (FormUrlUtils.isMap(rawValue) && FormUrlUtils.isArray(targetType)) {
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
            if (FormUrlUtils.isMap(rawValue)) {
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
        final Map<Object, Object> additionalProperties = initAdditionalProperties(model, field);
        additionalProperties.putAll(rawData);
    }

    /**
     * @param modelClass FormUrlEncoded model class
     * @return field annotated with FormUrlEncodedAdditionalProperties or null
     * @throws NullPointerException if modelClass parameter is null
     * @throws MarshallerException  if additionalProperties fields more than one
     * @throws MarshallerException  if additionalProperties type != {@code Map<String, String>}
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
     * @return additionalProperty field value (Map) or null if field not present
     * @throws NullPointerException if model parameter is null
     * @throws MarshallerException  if unable to initialize additionalProperties field
     * @throws MarshallerException  if additionalProperty field not readable
     */

    @SuppressWarnings("unchecked")
    protected Map<Object, Object> initAdditionalProperties(final Object model, final Field field) {
        FormUrlUtils.parameterRequireNonNull(model, MODEL_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        if (Modifier.isFinal(field.getModifiers())) {
            return (Map<Object, Object>) FormUrlUtils.readField(model, field);
        }
        final HashMap<Object, Object> value = new HashMap<>();
        FormUrlUtils.writeDeclaredField(model, field, value);
        return value;
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
                    .expected(ERR_SIMPLE_COMPLEX_REFERENCE_TYPE_ARRAY)
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
                    .errorMessage("Received non-convertible value")
                    .sourceType(value)
                    .sourceValue(value)
                    .targetType(Boolean.class)
                    .expectedValue("true || false")
                    .build();
        }
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
     * @return URL form data coding charset
     */
    public Charset getCodingCharset() {
        return codingCharset;
    }

}
