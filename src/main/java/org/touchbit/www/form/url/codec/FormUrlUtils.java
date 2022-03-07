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

package org.touchbit.www.form.url.codec;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.touchbit.www.form.url.codec.chain.IChainList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static org.touchbit.www.form.url.codec.CodecConstant.*;

/**
 * Convert model (JavaBean) to URL encoded form and back to model.
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
 *     Strung formUrlEncodedString = FormUrlEncodedMapper.INSTANCE.marshal(model);
 *     Model formUrlDecodedModel = FormUrlEncodedMapper.INSTANCE.unmarshal(formUrlEncodedString);
 * </code></pre>
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 19.02.2022
 * @see FormUrlEncoded
 * @see FormUrlEncodedField
 * @see FormUrlEncodedAdditionalProperties
 */
public class FormUrlUtils {

    private FormUrlUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param parameter     - checked parameter
     * @param parameterName - checked parameter name
     * @throws NullPointerException if parameter is null
     */
    public static void parameterRequireNonNull(Object parameter, String parameterName) {
        Objects.requireNonNull(parameter, "Parameter '" + parameterName + "' is required and cannot be null.");
    }


    public static boolean isConstantField(final Field field) {
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    public static boolean isNotAssignableConstantField(final Object object, final Field field) {
        FormUrlUtils.parameterRequireNonNull(object, "object");
        return isNotAssignableConstantField(object.getClass(), field);
    }

    public static boolean isNotAssignableConstantField(final Class<?> aClass, final Field field) {
        FormUrlUtils.parameterRequireNonNull(aClass, "aClass");
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        return !aClass.isAssignableFrom(field.getType()) && !isConstantField(field);
    }

    public static boolean isAssignableConstantField(final Object object, final Field field) {
        FormUrlUtils.parameterRequireNonNull(object, "object");
        return isAssignableConstantField(object.getClass(), field);
    }

    public static boolean isAssignableConstantField(final Class<?> aClass, final Field field) {
        FormUrlUtils.parameterRequireNonNull(aClass, "aClass");
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        return aClass.isAssignableFrom(field.getType()) && isConstantField(field);
    }

    public static boolean isJacocoDataField(final Field field) {
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        return field.getName().contains("jacocoData");
    }

    public static Object readField(final Object object, final Field field) {
        FormUrlUtils.parameterRequireNonNull(object, "object");
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        return readField(object, field.getName());
    }

    public static Object readField(final Object object, final String fieldName) {
        try {
            return FieldUtils.readField(object, fieldName, true);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get value from field: " + fieldName, e);
        }
    }


    public static boolean isGenericMap(final Field field) {
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final ParameterizedType type = getParameterizedType(field);
        return type != null && type.getRawType() == Map.class;
    }


    public static boolean isGenericMap(final Type type) {
        FormUrlUtils.parameterRequireNonNull(type, TYPE_PARAMETER);
        final ParameterizedType parameterizedType = getParameterizedType(type);
        return parameterizedType != null && parameterizedType.getRawType() == Map.class;
    }


    public static boolean isGenericCollection(final Field field) {
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final ParameterizedType type = getParameterizedType(field);
        return type != null && type.getRawType() == Collection.class;
    }


    public static boolean isGenericCollection(final Type type) {
        FormUrlUtils.parameterRequireNonNull(type, TYPE_PARAMETER);
        final ParameterizedType parameterizedType = getParameterizedType(type);
        return parameterizedType != null && parameterizedType.getRawType() == Collection.class;
    }


    public static boolean isArray(final Field field) {
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        return field.getType().isArray();
    }


    public static boolean isArray(final Type type) {
        FormUrlUtils.parameterRequireNonNull(type, TYPE_PARAMETER);
        return (type instanceof Class) && ((Class<?>) type).isArray();
    }


    public static ParameterizedType getParameterizedType(final Field field) {
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        final Type genericType = field.getGenericType();
        return getParameterizedType(genericType);
    }


    public static ParameterizedType getParameterizedType(final Type type) {
        FormUrlUtils.parameterRequireNonNull(type, TYPE_PARAMETER);
        if (type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        return null;
    }

    /**
     * @param value array || collection
     * @return {@link Collection}
     * @throws FormUrlCodecException if value is not array or collection
     */
    @SuppressWarnings({"java:S1452"})
    public static Collection<?> arrayToCollection(final Object value) {
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        if (isArray(value)) {
            return Arrays.asList((Object[]) value);
        }
        if (isCollection(value)) {
            return ((Collection<?>) value);
        }
        // TODO
        throw new RuntimeException("Received unsupported type to convert to collection: " + value.getClass());
    }

    /**
     * @param object - nullable object
     * @return true if object instanceof {@link Collection}
     */
    public static boolean isCollection(Object object) {
        return object instanceof Collection;
    }

    /**
     * @param object - nullable object
     * @return true if object instanceof {@link IChainList}
     */
    public static boolean isChainList(Object object) {
        return object instanceof IChainList;
    }

    public static boolean isCollection(Field field) {
        parameterRequireNonNull(field, FIELD_PARAMETER);
        return Collection.class.isAssignableFrom(field.getType());
    }

    /**
     * @param object - nullable object
     * @return true if object is array
     */
    public static boolean isArray(Object object) {
        return object != null && object.getClass().isArray();
    }

    /**
     * @param object - nullable object
     * @return true if object instanceof {@link Map}
     */
    public static boolean isMap(Object object) {
        return object instanceof Map;
    }

    /**
     * @param object - nullable object
     * @return true if object instanceof simple java data type (String, Integer, etc.)
     */
    public static boolean isSimple(Object object) {
        return object instanceof String ||
               object instanceof Boolean ||
               object instanceof Short ||
               object instanceof Long ||
               object instanceof Float ||
               object instanceof Integer ||
               object instanceof Double ||
               object instanceof BigInteger ||
               object instanceof BigDecimal;
    }

    /**
     * @param list - nullable {@link IChainList}
     * @return true if list contains only object instanceof simple java data type (String, Integer, etc.)
     */
    public static boolean isSimpleIChainList(IChainList list) {
        return list != null && list.stream().allMatch(FormUrlUtils::isSimple);
    }

    /**
     * @param list - nullable {@link IChainList}
     * @return true if list contains only object instanceof simple java data type (String, Integer, etc.)
     */
    public static boolean isMapIChainList(IChainList list) {
        return list != null && list.stream().allMatch(FormUrlUtils::isMap);
    }

}
