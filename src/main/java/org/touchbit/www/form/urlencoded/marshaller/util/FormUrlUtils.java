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
import org.apache.commons.lang3.reflect.TypeUtils;
import org.touchbit.www.form.urlencoded.marshaller.chain.IChainList;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static org.touchbit.www.form.urlencoded.marshaller.util.CodecConstant.*;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 19.02.2022
 */
public class FormUrlUtils {

    /**
     * Utility class. Forbidden instantiation.
     */
    private FormUrlUtils() {
    }

    /**
     * @param parameter     checked parameter
     * @param parameterName checked parameter name
     * @throws NullPointerException if parameter is null
     */
    public static void parameterRequireNonNull(final Object parameter, final String parameterName) {
        if (parameter == null) {
            throw new MarshallerException("Parameter '" + parameterName + "' is required and cannot be null.", null);
        }
    }

    public static Object readField(final Object object, final Field field) {
        FormUrlUtils.parameterRequireNonNull(object, OBJECT_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        try {
            return FieldUtils.readField(object, field.getName(), true);
        } catch (Exception e) {
            throw MarshallerException.builder()
                    .errorMessage("Unable to raed value from object field.")
                    .model(object)
                    .field(field)
                    .errorCause(e)
                    .build();
        }
    }

    /**
     * @param object target object for write
     * @param field  object field
     * @param value  field value
     * @throws MarshallerException if the value cannot be written to the model field
     */
    public static void writeDeclaredField(final Object object, final Field field, final Object value) {
        FormUrlUtils.parameterRequireNonNull(object, OBJECT_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(field, FIELD_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        try {
            FieldUtils.writeDeclaredField(object, field.getName(), value, true);
        } catch (Exception e) {
            throw MarshallerException.builder()
                    .errorMessage("Unable to write value to object field.")
                    .model(object)
                    .field(field)
                    .value(value)
                    .valueType(value)
                    .errorCause(e)
                    .build();
        }
    }

    public static boolean isGenericMap(final Type type) {
        final ParameterizedType parameterizedType = getParameterizedType(type);
        if (parameterizedType != null) {
            final Class<?> rawType = TypeUtils.getRawType(parameterizedType, null);
            return Map.class.isAssignableFrom(rawType);
        }
        return false;
    }

    public static boolean isGenericCollection(final Type type) {
        final ParameterizedType parameterizedType = getParameterizedType(type);
        if (parameterizedType != null) {
            final Class<?> rawType = TypeUtils.getRawType(parameterizedType, null);
            return Collection.class.isAssignableFrom(rawType);
        }
        return false;
    }

    /**
     * @param object nullable object
     * @return true if object is array
     */
    public static boolean isArray(final Object object) {
        return object != null && isArray(object.getClass());
    }

    public static boolean isArray(final Type type) {
        return (type instanceof Class) && ((Class<?>) type).isArray();
    }

    public static boolean isGenericArray(final Type type) {
        return type instanceof GenericArrayType;
    }

    public static ParameterizedType getParameterizedType(final Type type) {
        if (type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        return null;
    }

    /**
     * @param object nullable object
     * @return true if object instanceof {@link Collection}
     */
    public static boolean isCollection(final Object object) {
        if (object instanceof Type) {
            return isCollection((Type) object);
        }
        return object instanceof Collection;
    }

    /**
     * @param type nullable object type
     * @return true if object instanceof {@link Collection}
     */
    public static boolean isCollection(final Type type) {
        if (type != null) {
            final Class<?> rawType = TypeUtils.getRawType(type, null);
            return Collection.class.isAssignableFrom(rawType);
        }
        return false;
    }

    /**
     * @param object nullable object
     * @return true if object instanceof {@link IChainList}
     */
    public static boolean isChainList(final Object object) {
        return object instanceof IChainList;
    }

    /**
     * @param object nullable object
     * @return true if object instanceof {@link Map}
     */
    public static boolean isMapAssignableFrom(final Object object) {
        if (object != null) {
            final Class<?> objClass = (object instanceof Class) ? (Class<?>) object : object.getClass();
            return Map.class.isAssignableFrom(objClass);
        }
        return false;
    }

    /**
     * @param object nullable object
     * @return true if object class contains {@link FormUrlEncoded} annotation
     */
    public static boolean isPojo(final Object object) {
        if (object != null) {
            if (object instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType) object;
                return isPojo(parameterizedType.getRawType());
            }
            final Class<?> objClass = (object instanceof Class) ? ((Class<?>) object) : object.getClass();
            return objClass.isAnnotationPresent(FormUrlEncoded.class);
        }
        return false;
    }

    /**
     * @param object nullable object
     * @return true if object class contains {@link FormUrlEncoded} annotation
     */
    public static boolean isSomePojo(final Object object) {
        return isPojo(object) || isPojoGenericCollection(object) || isPojoArray(object);
    }

    public static boolean isPojoArray(final Object object) {
        if (object != null) {
            if (object instanceof Type) {
                final Type type = (Type) object;
                if (isArray(type)) {
                    final Type arrayComponentType = TypeUtils.getArrayComponentType(type);
                    return isPojo(arrayComponentType);
                }
                return false;
            } else {
                return isPojoArray(object.getClass());
            }
        }
        return false;
    }

    public static boolean isPojoGenericCollection(final Object object) {
        if (object != null) {
            if (object instanceof Type) {
                final Type type = (Type) object;
                if (isGenericCollection(type)) {
                    final Type genericType = getGenericCollectionArgumentType(type);
                    return isPojo(genericType);
                }
                return false;
            } else {
                return isPojoGenericCollection(object.getClass());
            }
        }
        return false;
    }

    public static Type getArrayComponentType(final Type type) {
        final Type arrayComponentType = TypeUtils.getArrayComponentType(type);
        if (arrayComponentType != null) {
            return arrayComponentType;
        }
        throw MarshallerException.builder()
                .errorMessage("Type is not an array.")
                .actualType(type)
                .expected("array type")
                .build();
    }

    public static Class<?> getArrayComponentClass(final Type type) {
        final Type arrayComponentType = getArrayComponentType(type);
        return TypeUtils.getRawType(arrayComponentType, null);
    }

    public static Object[] collectionToArray(final Collection<?> collection, final Class<?> componentType) {
        return collection.toArray((Object[]) Array.newInstance(componentType, 0));
    }

    public static Object[] objectToArray(final Object object, final Class<?> componentType) {
        return Collections.singletonList(object).toArray((Object[]) Array.newInstance(componentType, 0));
    }

    public static Class<?> getGenericCollectionArgumentRawType(final Type type) {
        final Type genericCollectionArgumentType = getGenericCollectionArgumentType(type);
        return TypeUtils.getRawType(genericCollectionArgumentType, genericCollectionArgumentType);
    }

    public static Type getGenericCollectionArgumentType(final Type type) {
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(parameterizedType);
            final List<Type> typeArgumentsList = typeArguments.keySet().stream().map(typeArguments::get)
                    .collect(Collectors.toList());
            if (typeArgumentsList.size() == 1) {
                return typeArgumentsList.get(0);
            }
            throw MarshallerException.builder()
                    .errorMessage("An incorrect number of TypeArguments was received for a generic collection.")
                    .expected(1)
                    .actual(typeArgumentsList.size())
                    .actualValue(typeArgumentsList)
                    .build();
        }
        throw MarshallerException.builder()
                .errorMessage("Received type is not a generic collection.")
                .sourceType(type)
                .expectedType(Collection.class)
                .build();
    }

    public static boolean hasAdditionalProperty(final Object object) {
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
                .filter(f -> !Modifier.isTransient(f.getModifiers()))
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .collect(Collectors.toList());
    }

    /**
     * @param object nullable object
     * @return true if object instanceof simple java data type (String, Integer, etc.)
     */
    public static boolean isSimple(final Object object) {
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
     * @param type nullable object type
     * @return true if object instanceof simple java data type (String, Integer, etc.)
     */
    public static boolean isSimple(final Type type) {
        if (type instanceof Class) {
            final Class<?> aClass = (Class<?>) type;
            return String.class.isAssignableFrom(aClass) ||
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
     * @param object nullable {@link Field} or {@link ParameterizedType} or {@link Type} or {@link Class}
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
        if (object instanceof Collection) {
            return ((Collection<?>) object).stream().filter(Objects::nonNull).allMatch(FormUrlUtils::isSimple);
        }
        return isCollection(object);
    }

    /**
     * @param object nullable {@link Field} or {@link Type} or {@link Class}
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
     * @param object nullable {@link Field} or {@link ParameterizedType} or {@link Type} or {@link Class}
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
        if (object instanceof Collection) {
            return ((Collection<?>) object).stream().allMatch(FormUrlUtils::isCollection);
        }
        return isCollection(object);
    }

    /**
     * @param object nullable {@link Field} or {@link ParameterizedType} or {@link Type} or {@link Class}
     * @return true if the object or field type is a collection and contains nested collections {@code [[], []]}
     */
    public static boolean isCollectionOfMap(final Object object) {
        if (object instanceof Field) {
            Field field = (Field) object;
            return isCollectionOfMap(field.getGenericType());
        }
        if (object instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) object;
            final Type objectRawType = parameterizedType.getRawType();
            final Type genericType = parameterizedType.getActualTypeArguments()[0];
            return isCollectionOfMap(objectRawType) && isGenericMap(genericType);
        }
        if (object instanceof Collection) {
            return ((Collection<?>) object).stream().allMatch(FormUrlUtils::isMapAssignableFrom);
        }
        return isCollection(object);
    }

    /**
     * @param list nullable {@link IChainList}
     * @return true if list contains only object instanceof simple java data type (String, Integer, etc.)
     */
    public static boolean isMapIChainList(final IChainList list) {
        return list != null && list.stream().allMatch(FormUrlUtils::isMapAssignableFrom);
    }

    public static <M> M invokeConstructor(final Class<M> modelClass) {
        try {
            return ConstructorUtils.invokeConstructor(modelClass);
        } catch (Exception e) {
            throw MarshallerException.builder()
                    .errorMessage("Unable to instantiate model class.")
                    .sourceType(modelClass)
                    .errorCause(e)
                    .build();
        }
    }

    /**
     * @param value form URL decoded string
     * @return form URL encoded string
     * @throws NullPointerException  if value is null
     * @throws IllegalStateException cannot be thrown since the configuration operates on an {@link java.nio.charset.Charset} class.
     */
    public static String encode(final String value, final Charset codingCharset) {
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(codingCharset, CODING_CHARSET_PARAMETER);
        try {
            return URLEncoder.encode(value, codingCharset.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unexpected error", e);
        }
    }

    /**
     * @param value form URL encoded string
     * @return form URL decoded string
     * @throws NullPointerException  if value is null
     * @throws IllegalStateException cannot be thrown since the configuration operates on an {@link java.nio.charset.Charset} class.
     */
    public static String decode(final Object value, final Charset codingCharset) {
        FormUrlUtils.parameterRequireNonNull(value, VALUE_PARAMETER);
        FormUrlUtils.parameterRequireNonNull(codingCharset, CODING_CHARSET_PARAMETER);
        try {
            return URLDecoder.decode(value.toString(), codingCharset.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unexpected error", e);
        }
    }

}
