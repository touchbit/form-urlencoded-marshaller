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

import org.touchbit.www.form.urlencoded.marshaller.chain.IChain;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;
import org.touchbit.www.form.urlencoded.marshaller.util.FormUrlCodecException;
import org.touchbit.www.form.urlencoded.marshaller.util.FormUrlUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static org.touchbit.www.form.urlencoded.marshaller.util.CodecConstant.MODEL_PARAMETER;
import static org.touchbit.www.form.urlencoded.marshaller.util.CodecConstant.VALUE_PARAMETER;

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

    private static final String CONVERSION_UNSUPPORTED_TYPE_ERR_MSG = "Received unsupported type for conversion: ";

    public static final FormUrlMarshaller INSTANCE = new FormUrlMarshaller();

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
     * @throws FormUrlCodecException on class instantiation errors
     */
    @Override
    public <M> M unmarshal(final Class<M> modelClass, final String encodedString) {
        return null;
    }

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
    protected String decode(String value) {
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        try {
            return URLDecoder.decode(value, getConfiguration().getCodingCharset().name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unexpected error", e);
        }
    }

}
