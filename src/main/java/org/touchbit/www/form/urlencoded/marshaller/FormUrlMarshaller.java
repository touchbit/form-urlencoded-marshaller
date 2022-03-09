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
import org.apache.commons.lang3.reflect.FieldUtils;
import org.touchbit.www.form.urlencoded.marshaller.chain.IChain;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;
import org.touchbit.www.form.urlencoded.marshaller.util.CodecConstant;
import org.touchbit.www.form.urlencoded.marshaller.util.FormUrlUtils;
import org.touchbit.www.form.urlencoded.marshaller.util.MarshallerException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
    private static final String UNABLE_READ_FIELD_VALUE_ERR_MSG = "Unable to read value from model field.\n";
    private static final String UNABLE_ENCODE_ERR_MSG = "Unable to encode string to FormUrlEncoded format.\n";
    private static final String MODEL_TYPE_ERR_MSG = "    Model type: ";
    private static final String FIELD_NAME_ERR_MSG = "    Field name: ";
    private static final String FORF_FIELD_NAME_ERR_MSG = "    URL form field name: ";
    private static final String VALUE_TO_ENCODE_ERR_MSG = "    Value to encode: ";
    private static final String ENCODE_CHARSET_ERR_MSG = "    Encode charset: ";
    private static final String ERROR_CAUSE_ERR_MSG = "    Error cause: ";
    private static final String FIELD_TYPE_ERR_MSG = "    Field type: ";
    private static final String VALUE_FOR_CONVERT_ERR_MSG = "    Value for convert: ";
    private static final String ANNOTATION_ERR_MSG = "    Annotation: ";
    private static final String MODEL_ERR_MSG = "    Model: ";
    private static final String FIELD_ERR_MSG = "    Field: ";
    private static final String ACT_TYPE_ERR_MSG = "    Actual type: ";
    private static final String EXP_TYPE_ERR_MSG = "    Expected type: ";
    private static final String CONVERSION_UNSUPPORTED_TYPE_ERR_MSG = "Received unsupported type for conversion: ";
    private final IFormUrlMarshallerConfiguration configuration;

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
     * @param model - {@code Map<String, Object>} or pojo object with {@link FormUrlEncoded} annotation
     * @return form url encoded string
     * @throws NullPointerException     if model is null
     * @throws IllegalArgumentException if model type is not supported
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
        throw new IllegalArgumentException(CONVERSION_UNSUPPORTED_TYPE_ERR_MSG + model.getClass());
    }

    /**
     * @param value - any object
     * @return converted value (List || Map || String)
     * @throws IllegalArgumentException if value type is not supported
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
            throw new IllegalArgumentException(CONVERSION_UNSUPPORTED_TYPE_ERR_MSG + value.getClass());
        }
    }

    /**
     * @param value - any object with {@link FormUrlEncoded} class annotation
     * @return {@link HashMap} with converted values
     * @throws NullPointerException     if value is null
     * @throws IllegalArgumentException class does not contain a FormUrlEncodedField annotation
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
                    resultValue = annotation.encoded() ? fieldValue : encode(String.valueOf(fieldValue));
                } else {
                    resultValue = convertValueToRawData(fieldValue);
                }
                result.put(resultKey, resultValue);
            }
            return result;
        }
        throw new IllegalArgumentException("Object class does not contain a required annotation.\n" +
                                           "Class: " + value.getClass().getName() + "\n" +
                                           "Expected annotation: " + FormUrlEncoded.class.getName() + "\n");
    }

    /**
     * @param value - any {@link Map}
     * @return {@link HashMap} with converted values
     * @throws NullPointerException     if value is null
     * @throws IllegalArgumentException if value is not {@link Map}
     * @throws IllegalArgumentException if map keys is not {@link String}
     */
    protected Map<String, Object> convertMapToRawData(final Object value) {
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        final Map<String, Object> result = new HashMap<>();
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            final Set<?> rawKeySet = map.keySet();
            final List<?> unsupportedKeys = rawKeySet.stream()
                    .filter(e -> !(e instanceof String))
                    .collect(Collectors.toList());
            if (!unsupportedKeys.isEmpty()) {
                throw new IllegalArgumentException("Keys in the map must be of type String: Map<String, Object>\n" +
                                                   "Unsupported keys: " + unsupportedKeys);
            }
            for (Object rawKey : rawKeySet) {
                final String resultKey = String.valueOf(rawKey);
                final Object resultValue = map.get(resultKey);
                final Object rawDataValue = convertValueToRawData(resultValue);
                result.put(resultKey, rawDataValue);
            }
            return result;
        }
        throw new IllegalArgumentException(CONVERSION_UNSUPPORTED_TYPE_ERR_MSG + "\n" +
                                           "Expected heirs of " + Map.class + "\n" +
                                           "Actual: " + value.getClass() + "\n");
    }

    /**
     * @param value - any {@link Collection}
     * @return {@link ArrayList} with converted array values
     * @throws NullPointerException     if value is null
     * @throws IllegalArgumentException if value is not {@link Collection}
     */
    protected Object convertCollectionToRawData(final Object value) {
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        final List<Object> result = new ArrayList<>();
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            collection.forEach(item -> result.add(convertValueToRawData(item)));
            return result;
        }
        throw new IllegalArgumentException(CONVERSION_UNSUPPORTED_TYPE_ERR_MSG + "\n" +
                                           "Expected heirs of " + Collection.class + "\n" +
                                           "Actual: " + value.getClass() + "\n");
    }

    /**
     * @param value - any array
     * @return {@link ArrayList} with converted array values
     * @throws NullPointerException     if value is null
     * @throws IllegalArgumentException if value is not array
     */
    protected Object convertArrayToRawData(final Object value) {
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        final List<Object> result = new ArrayList<>();
        if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            Arrays.stream(array).forEach(item -> result.add(convertValueToRawData(item)));
            return result;
        }
        throw new IllegalArgumentException(CONVERSION_UNSUPPORTED_TYPE_ERR_MSG + "\n" +
                                           "Expected: array\n" +
                                           "Actual: " + value.getClass().getName() + "\n");
    }

    /**
     * @param value - simple value (String, Integer, Double, etc.)
     * @return encoded value or empty string if value is null
     */
    protected Object convertSimpleToRawData(final Object value) {
        return encode(value == null ? "" : String.valueOf(value));
    }

    /**
     * String to model conversion
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @param modelClass    - FormUrlEncoded model class
     * @param encodedString - URL encoded string to conversation
     * @param <M>           - model generic type
     * @return completed model
     * @throws MarshallerException on class instantiation errors
     */
    @Override
    public <M> M unmarshal(final Class<M> modelClass, final String encodedString) {
        final Map<String, Object> rawData = new IChain.Default(encodedString).getRawData();
        final M model = FormUrlUtils.invokeModelConstructor(modelClass);
        if (FormUrlUtils.isMap(model) || FormUrlUtils.isPojo(model)) {
            writeRawDataToPojo(model, rawData);
            return model;
        }
        throw new IllegalArgumentException(CONVERSION_UNSUPPORTED_TYPE_ERR_MSG + model.getClass());
    }

    protected <M> void writeRawDataToPojo(final M model, final Map<String, Object> rawData) {
        FormUrlUtils.parameterRequireNonNull(model, MODEL_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(rawData, RAW_DATA_PARAMETER);
        final Map<String, Field> pojoFields = FormUrlUtils.getFormUrlEncodedFieldsMap(model);
        for (String fieldName : pojoFields.keySet()) {
            final Object rawValue = rawData.get(fieldName);
            if (rawValue != null) {
                final Field pojoField = pojoFields.get(fieldName);
                writeValueToField(model, pojoField, rawValue);
                rawData.remove(fieldName);
            }
        }
        if (FormUrlUtils.hasAdditionalProperty(model) && !rawData.isEmpty()) {
            unmarshalAndWriteAdditionalProperties(model, rawData);
        }
    }

    protected <M> void writeValueToField(final M model, final Field field, Object value) {
        if (FormUrlUtils.isSimple(field)) {
            final String decoded = decode(value);
            final Object fieldValue = convertUrlDecodedStringValueToSimpleType(decoded, field.getType());
            writeFieldValue(model, field, fieldValue);
        } else if (FormUrlUtils.isCollection(field)) {
            final Collection<?> rawCollection = (Collection<?>) value;
            writeCollectionToField(model, field, rawCollection);
        } else if (FormUrlUtils.isMap(field)) {
            // todo
        } else {
            throw new MarshallerException("TODO unsupported value type");
        }
    }

    protected <M> void writeCollectionToField(final M model, final Field field, Collection<?> value) {
        final Type fieldType = field.getGenericType();
        final Object fieldValue = convertRawValueToTargetType(value, fieldType);
        writeFieldValue(model, field, fieldValue);
    }

    protected Object convertRawValueToTargetType(final Object rawValue, Type targetType) {
        if (FormUrlUtils.isSimple(rawValue) && FormUrlUtils.isSimple(targetType)) {
            // raw value String to target type Integer
            final String decoded = decode(rawValue);
            return convertUrlDecodedStringValueToSimpleType(decoded, targetType);
        }
        if (FormUrlUtils.isSimple(rawValue) && FormUrlUtils.isGenericCollection(targetType)) {
            // raw value String to target type List<Integer> (hidden url encoded array)
            final ParameterizedType parameterizedTargetType = (ParameterizedType) targetType;
            final Type targetGenericType = parameterizedTargetType.getActualTypeArguments()[0];
            final String decoded = decode(rawValue);
            final Object value = convertUrlDecodedStringValueToSimpleType(decoded, targetGenericType);
            return Collections.singletonList(value);
        }
        if (FormUrlUtils.isSimple(rawValue) && FormUrlUtils.isArray(targetType)) {
            // raw value String to target type Integer[] (hidden url encoded array)
            final Class<?> componentType = ((Class<?>) targetType).getComponentType();
            final String decoded = decode(rawValue);
            return Collections.singletonList(convertUrlDecodedStringValueToSimpleType(decoded, componentType))
                    .toArray((Object[]) Array.newInstance(componentType, 0));
        }
        if (FormUrlUtils.isMap(rawValue) && FormUrlUtils.isGenericMap(targetType)) {
            final Map<?, ?> rawMap = (Map<?, ?>) rawValue;
            final ParameterizedType parameterizedTargetType = (ParameterizedType) targetType;
            final Type targetGenericValueType = parameterizedTargetType.getActualTypeArguments()[1];
            if (targetGenericValueType.equals(Object.class)) {
                // Target type Map<String, Object>. No need for conversion.
                return rawValue;
            }
            final List<String> keys = rawMap.keySet().stream().map(Object::toString).collect(Collectors.toList());
            final Map<String, Object> result = new HashMap<>();
            for (String key : keys) {
                final Object rawMapValue = rawMap.get(key);
                final Object resultValue = convertRawValueToTargetType(rawMapValue, targetGenericValueType);
                result.put(key, resultValue);
            }
            return result;
        }
        if (FormUrlUtils.isMap(rawValue) && FormUrlUtils.isMap(targetType)) {
            // raw value Map<String, Object> to target type raw Map (raw map without generic)
            return rawValue;
        }
        if (FormUrlUtils.isMap(rawValue) && FormUrlUtils.isArray(targetType)) {
            // raw value Map<String, ?> to target type raw Map[] (hidden url encoded array)
            final Class<?> targetClass = (Class<?>) targetType;
            final Class<?> componentType = targetClass.getComponentType();
            final Object value = convertRawValueToTargetType(rawValue, componentType);
            return Collections.singletonList(value).toArray((Object[]) Array.newInstance(componentType, 0));
        }
        if (FormUrlUtils.isCollectionOfMap(rawValue) && FormUrlUtils.isGenericArray(targetType)) {
            final Collection<?> rawCollection = (Collection<?>) rawValue;
            final GenericArrayType genericArrayType = (GenericArrayType) targetType;
            final Type genericComponentType = genericArrayType.getGenericComponentType();
            final Class<?> genericClass;
            if (genericComponentType instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType) genericComponentType;
                genericClass = (Class<?>) parameterizedType.getRawType();
            } else {
                genericClass = (Class<?>) genericComponentType;
            }
            return rawCollection.stream()
                    .map(i -> convertRawValueToTargetType(i, genericClass))
                    .collect(Collectors.toList())
                    .toArray((Object[]) Array.newInstance(genericClass, 0));
        }
        if (FormUrlUtils.isMap(rawValue) && FormUrlUtils.isGenericArray(targetType)) {
            // raw value Map<String, ?> to target type Map<String, ?>[] (hidden url encoded array)
            final GenericArrayType genericArrayType = (GenericArrayType) targetType;
            final Type genericComponentType = genericArrayType.getGenericComponentType();
            final Class<?> genericClass;
            if (genericComponentType instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType) genericComponentType;
                genericClass = (Class<?>) parameterizedType.getRawType();
            } else {
                genericClass = (Class<?>) genericComponentType;
            }
            final Object value = convertRawValueToTargetType(rawValue, genericComponentType);
            return Collections.singletonList(value).toArray((Object[]) Array.newInstance(genericClass, 0));
        }
        if (FormUrlUtils.isMap(rawValue) && FormUrlUtils.isGenericCollection(targetType)) {
            // raw value Map<String, Object> to target type List<Map<?, ?>> (hidden url encoded array)
            final ParameterizedType parameterizedTargetType = (ParameterizedType) targetType;
            final Type targetGenericType = parameterizedTargetType.getActualTypeArguments()[0];
            final Object value = convertRawValueToTargetType(rawValue, targetGenericType);
            return Collections.singletonList(value);
        }
        if (FormUrlUtils.isCollection(rawValue) && FormUrlUtils.isArray(targetType)) {
            // raw value List<String> to target type Integer[]
            final Collection<?> rawCollection = (Collection<?>) rawValue;
            final Class<?> componentType = ((Class<?>) targetType).getComponentType();
            return rawCollection.stream()
                    .map(this::decode)
                    .map(i -> convertUrlDecodedStringValueToSimpleType(i, componentType))
                    .collect(Collectors.toList())
                    .toArray((Object[]) Array.newInstance(componentType, 0));
        }
        // raw value List<Object> to target type List (raw generic list)
        if (FormUrlUtils.isCollection(rawValue) && FormUrlUtils.isCollection(targetType)) {
            return rawValue;
        }
        if (FormUrlUtils.isCollection(rawValue) && FormUrlUtils.isGenericCollection(targetType)) {
            final Collection<?> rawCollection = (Collection<?>) rawValue;
            final ParameterizedType parameterizedTargetType = (ParameterizedType) targetType;
            final Type targetGenericType = parameterizedTargetType.getActualTypeArguments()[0];
            // raw value List<Map<?, ?>> to target type List<Map<?, ?>>
            if (FormUrlUtils.isCollectionOfMap(rawValue) && FormUrlUtils.isCollectionOfMap(targetType)) {
                return rawCollection.stream()
                        .map(i -> convertRawValueToTargetType(i, targetGenericType))
                        .collect(Collectors.toList());
            }
            // raw value List<String> to target type List<Object>
            if (targetGenericType.equals(Object.class)) {
                return rawValue;
            }
            // raw value List<List<String>> to target type List<List<Integer>>
            if (FormUrlUtils.isCollectionOfCollections(rawValue) && FormUrlUtils.isCollectionOfCollections(targetType)) {
                return rawCollection.stream()
                        .map(i -> convertRawValueToTargetType(i, targetGenericType))
                        .collect(Collectors.toList());
            }
            // raw value List<List<String>> to target type List<Integer[]>
            if (FormUrlUtils.isCollectionOfCollections(rawValue) && FormUrlUtils.isCollectionOfArray(targetType)) {
                final ParameterizedType parameterizedType = (ParameterizedType) targetType;
                final Class<?> genericClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                return rawCollection.stream()
                        .map(i -> convertRawValueToTargetType(i, genericClass))
                        .collect(Collectors.toList());
            }
            // raw value List<String> to target type List<Integer>
            if (FormUrlUtils.isCollectionOfSimpleObj(rawValue) && FormUrlUtils.isCollectionOfSimpleObj(targetType)) {
                return rawCollection.stream()
                        .map(this::decode)
                        .map(i -> convertUrlDecodedStringValueToSimpleType(i, targetGenericType))
                        .collect(Collectors.toList());
            }
        }
        final String sourceTypeName = (rawValue == null ? null : rawValue.getClass().getName());
        final String targetTypeName = targetType.getTypeName();
        throw new MarshallerException("Incompatible types received for conversion\n" +
                                      "    Source value: " + rawValue + "\n" +
                                      "    Source type: " + sourceTypeName + "\n" +
                                      "    Target type: " + targetTypeName + "\n");
    }

    /**
     * Populates the field marked with the {@link FormUrlEncodedField} annotation
     * with the received data that is not in the model.
     *
     * @param model   - FormUrlEncoded model
     * @param rawData - TODO
     * @param <M>     - model generic type
     * @throws MarshallerException if unable to initialize additionalProperties field
     */

    protected <M> void unmarshalAndWriteAdditionalProperties(final M model,
                                                             final Map<String, Object> rawData) {
        FormUrlUtils.parameterRequireNonNull(model, MODEL_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(rawData, RAW_DATA_PARAMETER);
        final Field field = getAdditionalPropertiesField(model.getClass());
        final Map<String, Object> additionalProperties = initAdditionalProperties(model, field);
        additionalProperties.putAll(rawData);
    }

    /**
     * @param modelClass - FormUrlEncoded model class
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
            final String fNames = additionalProperties.stream().map(Field::getName).collect(Collectors.joining(", "));
            throw new MarshallerException("Model contains more than one field annotated with " +
                                          FormUrlEncodedAdditionalProperties.class.getSimpleName() + ":\n" +
                                          MODEL_ERR_MSG + modelClass + "\n" +
                                          "    Fields: " + fNames + "\n");
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
            throw new MarshallerException("Invalid field type with @" +
                                          FormUrlEncodedAdditionalProperties.class.getSimpleName() + " annotation\n" +
                                          MODEL_ERR_MSG + modelClass + "\n" +
                                          FIELD_ERR_MSG + additionalProperty.getName() + "\n" +
                                          ACT_TYPE_ERR_MSG + type.getTypeName() + "\n" +
                                          EXP_TYPE_ERR_MSG + "java.util.Map<java.lang.String, java.lang.Object>\n");
        }
        return additionalProperty;
    }

    /**
     * If a field marked with the {@link FormUrlEncodedAdditionalProperties} annotation is present and not initialized,
     * then a new HashMap instance will be written to the field value.
     *
     * @param model - FormUrlEncoded model
     * @return additionalProperty field value (Map) or null if field not present
     * @throws NullPointerException if model parameter is null
     * @throws MarshallerException  if unable to initialize additionalProperties field
     * @throws MarshallerException  if additionalProperty field not readable
     */

    @SuppressWarnings("unchecked")
    protected Map<String, Object> initAdditionalProperties(final Object model, final Field field) {
        FormUrlUtils.parameterRequireNonNull(model, MODEL_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final String fieldName = field.getName();
        try {
            if (Modifier.isFinal(field.getModifiers())) {
                return (Map<String, Object>) FieldUtils.readDeclaredField(model, field.getName(), true);
            }
        } catch (Exception e) {
            throw new MarshallerException("Unable to read additional properties field.\n" +
                                          MODEL_ERR_MSG + model.getClass().getName() + "\n" +
                                          FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                                          FIELD_TYPE_ERR_MSG + field.getType() + "\n" +
                                          ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
        }
        try {
            final HashMap<String, Object> value = new HashMap<>();
            FieldUtils.writeDeclaredField(model, fieldName, value, true);
            return value;
        } catch (Exception e) {
            throw new MarshallerException("Unable to initialize additional properties field.\n" +
                                          MODEL_ERR_MSG + model.getClass().getName() + "\n" +
                                          FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                                          FIELD_TYPE_ERR_MSG + field.getType() + "\n" +
                                          ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
        }
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
     * @param value      - String value to convert
     * @param targetType - Java type to which the string is converted
     * @return converted value
     * @throws IllegalArgumentException if targetType is primitive
     * @throws IllegalArgumentException if targetType not supported
     * @throws IllegalArgumentException if the value cannot be converted to {@link Boolean} type
     * @throws NumberFormatException    if the value cannot be converted to number types
     */
    @SuppressWarnings("java:S3776")
    protected Object convertUrlDecodedStringValueToSimpleType(final String value, final Type targetType) {
        FormUrlUtils.parameterRequireNonNull(targetType, TARGET_TYPE_PARAMETER);
        if (targetType instanceof Class && ((Class<?>) targetType).isPrimitive()) {
            throw new IllegalArgumentException("It is forbidden to use primitive types " +
                                               "in FormUrlEncoded models: " + targetType);
        }
        if (value == null) {
            return null;
        }
        if (targetType.equals(String.class) || targetType.equals(Object.class)) {
            return value;
        }
        if (targetType.equals(Boolean.class)) {
            if ("true".equals(value)) {
                return true;
            }
            if ("false".equals(value)) {
                return false;
            }
            throw new IllegalArgumentException("Cannot convert string to boolean: '" + value + "'");
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
        throw new IllegalArgumentException(CONVERSION_UNSUPPORTED_TYPE_ERR_MSG + targetType);
    }

    /**
     * @param model - FormUrlEncoded model
     * @param field - model field
     * @param value - String value to convert
     * @param <M>   model generic type
     * @throws MarshallerException if the value cannot be written to the model field
     */

    protected <M> void writeFieldValue(final M model, final Field field, final Object value) {
        FormUrlUtils.parameterRequireNonNull(model, MODEL_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        try {
            FieldUtils.writeDeclaredField(model, field.getName(), value, true);
        } catch (Exception e) {
            final String fieldTypeName = field.getType().getSimpleName();
            final String fieldValue;
            if (value.getClass().isArray()) {
                fieldValue = Arrays.toString((Object[]) value);
            } else {
                fieldValue = String.valueOf(value);
            }
            throw new MarshallerException("Unable to write value to model field.\n" +
                                          MODEL_ERR_MSG + model.getClass().getName() + "\n" +
                                          FIELD_NAME_ERR_MSG + field.getName() + "\n" +
                                          FIELD_TYPE_ERR_MSG + fieldTypeName + "\n" +
                                          "    Value type: " + value.getClass().getSimpleName() + "\n" +
                                          "    Value: " + fieldValue + "\n" +
                                          ERROR_CAUSE_ERR_MSG + e.getMessage().trim() + "\n", e);
        }
    }

//    /**
//     * @param value - any object
//     * @return converted value (List || Map || String)
//     * @throws IllegalArgumentException if value type is not supported
//     */
//    protected Object convertValueToRawData(final Object value) {
//        if (value == null || FormUrlUtils.isSimple(value)) {
//            return convertSimpleToRawData(value);
//        } else if (FormUrlUtils.isPojo(value) || FormUrlUtils.isMap(value)) {
//            final Map<String, Object> map = new HashMap<>();
//            if (FormUrlUtils.isPojo(value)) {
//                map.putAll(convertPojoToRawData(value));
//            }
//            if (FormUrlUtils.isMap(value)) {
//                map.putAll(convertMapToRawData(value));
//            }
//            return map;
//        } else if (FormUrlUtils.isCollection(value)) {
//            return convertCollectionToRawData(value);
//        } else if (FormUrlUtils.isArray(value)) {
//            return convertArrayToRawData(value);
//        } else {
//            throw new IllegalArgumentException(CONVERSION_UNSUPPORTED_TYPE_ERR_MSG + value.getClass());
//        }
//    }

    /**
     * @param value - form URL decoded string
     * @return form URL encoded string
     * @throws NullPointerException  if value is null
     * @throws IllegalStateException cannot be thrown since the configuration operates on an {@link java.nio.charset.Charset} class.
     */
    protected String encode(String value) {
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        try {
            return URLEncoder.encode(value, getConfiguration().getCodingCharset().name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unexpected error", e);
        }
    }

    /**
     * @param value - form URL encoded string
     * @return form URL decoded string
     * @throws NullPointerException  if value is null
     * @throws IllegalStateException cannot be thrown since the configuration operates on an {@link java.nio.charset.Charset} class.
     */
    protected String decode(Object value) {
        try {
            return value == null ? null :
                    URLDecoder.decode(value.toString(), getConfiguration().getCodingCharset().name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unexpected error", e);
        }
    }

}
