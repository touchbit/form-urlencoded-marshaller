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

package org.touchbit.www.form.urlencoded.marshaller.util;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.touchbit.www.form.urlencoded.marshaller.chain.IChainList;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static org.touchbit.www.form.urlencoded.marshaller.util.CodecConstant.A_CLASS_PARAMETER;
import static org.touchbit.www.form.urlencoded.marshaller.util.CodecConstant.OBJECT_PARAMETER;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 19.02.2022
 */
public class FormUrlUtils {

    /**
     * Utility class. Forbidden instantiation.
     */
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
        FormUrlUtils.parameterRequireNonNull(field, CodecConstant.FIELD_PARAMETER);
        final int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    public static boolean isNotAssignableConstantField(final Object object, final Field field) {
        FormUrlUtils.parameterRequireNonNull(object, "object");
        return isNotAssignableConstantField(object.getClass(), field);
    }

    public static boolean isNotAssignableConstantField(final Class<?> aClass, final Field field) {
        FormUrlUtils.parameterRequireNonNull(aClass, "aClass");
        FormUrlUtils.parameterRequireNonNull(field, CodecConstant.FIELD_PARAMETER);
        return !aClass.isAssignableFrom(field.getType()) && !isConstantField(field);
    }

    public static boolean isAssignableConstantField(final Object object, final Field field) {
        FormUrlUtils.parameterRequireNonNull(object, "object");
        return isAssignableConstantField(object.getClass(), field);
    }

    public static boolean isAssignableConstantField(final Class<?> aClass, final Field field) {
        FormUrlUtils.parameterRequireNonNull(aClass, "aClass");
        FormUrlUtils.parameterRequireNonNull(field, CodecConstant.FIELD_PARAMETER);
        return aClass.isAssignableFrom(field.getType()) && isConstantField(field);
    }

    public static boolean isJacocoDataField(final Field field) {
        FormUrlUtils.parameterRequireNonNull(field, CodecConstant.FIELD_PARAMETER);
        return field.getName().contains("jacocoData");
    }

    public static Object readField(final Object object, final Field field) {
        FormUrlUtils.parameterRequireNonNull(object, "object");
        FormUrlUtils.parameterRequireNonNull(field, CodecConstant.FIELD_PARAMETER);
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
        FormUrlUtils.parameterRequireNonNull(field, CodecConstant.FIELD_PARAMETER);
        final ParameterizedType type = getParameterizedType(field);
        return type != null && type.getRawType() == Map.class;
    }

    public static boolean isGenericMap(final Type type) {
        FormUrlUtils.parameterRequireNonNull(type, CodecConstant.TYPE_PARAMETER);
        final ParameterizedType parameterizedType = getParameterizedType(type);
        return parameterizedType != null && parameterizedType.getRawType() == Map.class;
    }

    public static boolean isGenericCollection(final Field field) {
        FormUrlUtils.parameterRequireNonNull(field, CodecConstant.FIELD_PARAMETER);
        final ParameterizedType type = getParameterizedType(field);
        return type != null && type.getRawType() == Collection.class;
    }

    public static boolean isGenericCollection(final Type type) {
        FormUrlUtils.parameterRequireNonNull(type, CodecConstant.TYPE_PARAMETER);
        final ParameterizedType parameterizedType = getParameterizedType(type);
        return parameterizedType != null &&
               parameterizedType.getRawType() instanceof Class &&
               Collection.class.isAssignableFrom((Class<?>) parameterizedType.getRawType());
    }


    public static boolean isArray(final Field field) {
        return field != null && isArray(field.getType());
    }

    public static boolean isArray(final Type type) {
        return (type instanceof Class) && ((Class<?>) type).isArray();
    }

    public static ParameterizedType getParameterizedType(final Field field) {
        FormUrlUtils.parameterRequireNonNull(field, CodecConstant.FIELD_PARAMETER);
        final Type genericType = field.getGenericType();
        return getParameterizedType(genericType);
    }


    public static ParameterizedType getParameterizedType(final Type type) {
        FormUrlUtils.parameterRequireNonNull(type, CodecConstant.TYPE_PARAMETER);
        if (type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        return null;
    }

    /**
     * @param value array || collection
     * @return {@link Collection}
     * @throws MarshallerException if value is not array or collection
     */
    @SuppressWarnings({"java:S1452"})
    public static Collection<?> arrayToCollection(final Object value) {
        FormUrlUtils.parameterRequireNonNull(value, CodecConstant.VALUE_PARAMETER);
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
     * @param type - nullable object type
     * @return true if object instanceof {@link Collection}
     */
    public static boolean isCollection(Type type) {
        return type instanceof Class && Collection.class.isAssignableFrom((Class<?>) type);
    }

    /**
     * @param object - nullable object
     * @return true if object instanceof {@link IChainList}
     */
    public static boolean isChainList(Object object) {
        return object instanceof IChainList;
    }

    /**
     * @param field - an object field
     * @return true if object field instanceof {@link Collection}
     */
    public static boolean isCollection(Field field) {
        parameterRequireNonNull(field, CodecConstant.FIELD_PARAMETER);
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
     * @param field - nullable {@link Field}
     * @return true if field type instanceof {@link Map}
     */
    public static boolean isMap(Field field) {
        return field != null && isMap(field.getType());
    }

    /**
     * @param object - nullable object
     * @return true if object instanceof {@link Map}
     */
    public static boolean isMap(Object object) {
        if (object != null) {
            final Class<?> objClass = (object instanceof Class) ? (Class<?>) object : object.getClass();
            return Map.class.isAssignableFrom(objClass);
        }
        return false;
    }

    /**
     * @param object - nullable object
     * @return true if object class contains {@link FormUrlEncoded} annotation
     */
    public static boolean isPojo(Object object) {
        final Class<?> objClass = (object instanceof Class) ? ((Class<?>) object) : object.getClass();
        return objClass.isAnnotationPresent(FormUrlEncoded.class);
    }

    public static boolean hasAdditionalProperty(Object object) {
        final Class<?> objClass = (object instanceof Class) ? ((Class<?>) object) : object.getClass();
        return !FieldUtils.getFieldsListWithAnnotation(objClass, FormUrlEncodedAdditionalProperties.class).isEmpty();
    }

    public static List<Field> getFormUrlEncodedFields(final Object object) {
        parameterRequireNonNull(object, OBJECT_PARAMETER);
        return getFormUrlEncodedFields(object.getClass());
    }

    public static Map<String, Field> getFormUrlEncodedFieldsMap(final Object object) {
        parameterRequireNonNull(object, OBJECT_PARAMETER);
        return getFormUrlEncodedFieldsMap(object.getClass());
    }

    public static Map<String, Field> getFormUrlEncodedFieldsMap(final Class<?> aClass) {
        parameterRequireNonNull(aClass, A_CLASS_PARAMETER);
        return getFormUrlEncodedFields(aClass).stream()
                .collect(Collectors.toMap(f -> f.getAnnotation(FormUrlEncodedField.class).value(), f -> f));
    }

    public static List<Field> getFormUrlEncodedFields(final Class<?> aClass) {
        parameterRequireNonNull(aClass, A_CLASS_PARAMETER);
        return FieldUtils.getFieldsListWithAnnotation(aClass, FormUrlEncodedField.class).stream()
                .filter(f -> !f.getAnnotation(FormUrlEncodedField.class).value().trim().isEmpty())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .collect(Collectors.toList());
    }

    /**
     * @param field - nullable {@link Field}
     * @return true if field type instanceof simple java data type (String, Integer, etc.)
     */
    public static boolean isSimple(Field field) {
        return field != null && isSimple(field.getType());
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
     * @param type - nullable object type
     * @return true if object instanceof simple java data type (String, Integer, etc.)
     */
    public static boolean isSimple(Type type) {
        if (type instanceof Class) {
            final Class<?> aClass = (Class<?>) type;
            return String.class.isAssignableFrom(aClass) ||
                   Boolean.class.isAssignableFrom(aClass) ||
                   Boolean.class.isAssignableFrom(aClass) ||
                   Short.class.isAssignableFrom(aClass) ||
                   Long.class.isAssignableFrom(aClass) ||
                   Float.class.isAssignableFrom(aClass) ||
                   Integer.class.isAssignableFrom(aClass) ||
                   Double.class.isAssignableFrom(aClass) ||
                   BigInteger.class.isAssignableFrom(aClass) ||
                   BigDecimal.class.isAssignableFrom(aClass);
        }
        return false;
    }

    /**
     * @param object - nullable {@link Field} or {@link ParameterizedType} or {@link Type} or {@link Class}
     * @return true if the object or field type is a collection and contains simple types {@code ["foo", "bar"]}
     */
    public static boolean isCollectionOfSimpleObj(final Object object) {
        if (object instanceof Field) {
            Field field = (Field) object;
            return isCollectionOfSimpleObj(field.getType());
        }
        if (object instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) object;
            final Type objectRawType = parameterizedType.getRawType();
            final Type genericType = parameterizedType.getActualTypeArguments()[0];
            return isCollectionOfSimpleObj(objectRawType) && isSimple(genericType);
        }
        if (object instanceof Class) {
            return Collection.class.isAssignableFrom((Class<?>) object);
        }
        if (object instanceof Collection) {
            return ((Collection<?>) object).stream().allMatch(FormUrlUtils::isSimple);
        }
        return false;
    }

    /**
     * @param object - nullable {@link Field} or {@link Type} or {@link Class}
     * @return true if the object or field type is a collection and contains nested collections {@code [[], []]}
     */
    public static boolean isCollectionOfArray(final Object object) {
        if (object instanceof Field) {
            Field field = (Field) object;
            return isCollectionOfArray(field.getType());
        }
        if (object instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) object;
            final Type genericType = parameterizedType.getActualTypeArguments()[0];
            return isArray(genericType);
        }
        if (object instanceof Class) {
            return ((Class<?>) object).isArray();
        }
        return false;
    }

    /**
     * @param object - nullable {@link Field} or {@link ParameterizedType} or {@link Type} or {@link Class}
     * @return true if the object or field type is a collection and contains nested collections {@code [[], []]}
     */
    public static boolean isCollectionOfCollections(final Object object) {
        if (object instanceof Field) {
            Field field = (Field) object;
            return isCollectionOfCollections(field.getGenericType());
        }
        if (object instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) object;
            final Type objectRawType = parameterizedType.getRawType();
            final Type genericType = parameterizedType.getActualTypeArguments()[0];
            return isCollectionOfCollections(objectRawType) && isGenericCollection(genericType);
        }
        if (object instanceof Class) {
            return Collection.class.isAssignableFrom((Class<?>) object);
        }
        if (object instanceof Collection) {
            return ((Collection<?>) object).stream().allMatch(FormUrlUtils::isCollection);
        }
        return false;
    }

    /**
     * @param list - nullable list
     * @return true if list contains only object instanceof simple java data type (String, Integer, etc.)
     */
    public static boolean isCollectionOfMaps(final Collection<?> list) {
        return list != null && list.stream().allMatch(FormUrlUtils::isMap);
    }

    /**
     * @param list - nullable {@link IChainList}
     * @return true if list contains only object instanceof simple java data type (String, Integer, etc.)
     */
    public static boolean isMapIChainList(IChainList list) {
        return list != null && list.stream().allMatch(FormUrlUtils::isMap);
    }

    public static <M> M invokeModelConstructor(Class<M> modelClass) {
        try {
            return ConstructorUtils.invokeConstructor(modelClass);
        } catch (Exception e) {
            throw new MarshallerException("Unable to instantiate model class\n" +
                                          "    Model type: " + modelClass.getName() + "\n" +
                                          "    Error cause: " + e.getMessage().trim() + "\n", e);
        }
    }

}
