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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * Builder for {@link MarshallerException} and {@link ChainException}
 * Example: {@code throw MarshallerException.builder().errorMessage("message").actualType(value).build()}
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 11.03.2022
 */
public abstract class ExceptionBuilder<E extends RuntimeException> {

    /***/
    private String errorMessage = "\n";
    /***/
    private Exception cause = null;
    /***/
    private final StringJoiner additionalInfo = new StringJoiner("\n");
    /***/
    protected static final String L_DELIMITER = "\n     - ";

    /**
     * @return heirs of RuntimeException
     */
    public abstract E build();

    /**
     * @param errorMessage {@link RuntimeException#getMessage()}
     * @return this
     */
    public ExceptionBuilder<E> errorMessage(final String errorMessage) {
        this.errorMessage = errorMessage + "\n";
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param targetType nullable {@link Type}
     * @return this
     */
    public ExceptionBuilder<E> targetType(final Type targetType) {
        this.additionalInfo.add("    Target type: " + value(Type::getTypeName, targetType));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param object nullable object
     * @return this
     */
    public ExceptionBuilder<E> sourceType(final Object object) {
        final Class<?> aClass = value(Object::getClass, object);
        return sourceType(aClass);
    }

    /**
     * Add additional info to Exception message
     *
     * @param sourceType nullable {@link Type}
     * @return this
     */
    public ExceptionBuilder<E> sourceType(final Type sourceType) {
        this.additionalInfo.add("    Source type: " + value(Type::getTypeName, sourceType));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param object nullable object
     * @return this
     */
    public ExceptionBuilder<E> actualType(final Object object) {
        return object == null ? actualType(null) : actualType(object.getClass());
    }

    /**
     * Add additional info to Exception message
     *
     * @param actualType nullable {@link Type}
     * @return this
     */
    public ExceptionBuilder<E> actualType(final Type actualType) {
        this.additionalInfo.add("    Actual type: " + value(Type::getTypeName, actualType));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param expectedType nullable {@link Type}
     * @return this
     */
    public ExceptionBuilder<E> expectedType(final Type expectedType) {
        this.additionalInfo.add("    Expected type: " + value(Type::getTypeName, expectedType));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param object nullable object
     * @return this
     */
    public ExceptionBuilder<E> expectedValue(final Object object) {
        this.additionalInfo.add("    Expected value: " + value(String::valueOf, object));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param object nullable object
     * @return this
     */
    public ExceptionBuilder<E> source(final Object object) {
        this.additionalInfo.add("    Source: " + value(String::valueOf, object));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param object nullable object
     * @return this
     */
    public ExceptionBuilder<E> expected(final Object object) {
        this.additionalInfo.add("    Expected: " + value(String::valueOf, object));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param type nullable {@link Type}
     * @return this
     */
    public ExceptionBuilder<E> expectedHeirsOf(final Type type) {
        this.additionalInfo.add("    Expected: heirs of " + value(Type::getTypeName, type));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param object nullable object
     * @return this
     */
    public ExceptionBuilder<E> model(final Object object) {
        return model(value(Object::getClass, object));
    }

    /**
     * Add additional info to Exception message
     *
     * @param type nullable {@link Type}
     * @return this
     */
    public ExceptionBuilder<E> model(final Type type) {
        this.additionalInfo.add("    Model: " + value(Type::getTypeName, type));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param type nullable {@link Type}
     * @return this
     */
    public ExceptionBuilder<E> annotation(final Class<?> type) {
        this.additionalInfo.add("    Annotation: @" + value(Class::getSimpleName, type));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param type nullable {@link Type}
     * @return this
     */
    public ExceptionBuilder<E> annotationType(final Type type) {
        this.additionalInfo.add("    Annotation type: " + value(Type::getTypeName, type));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param fields nullable {@link Field} list
     * @return this
     */
    public ExceptionBuilder<E> fields(final List<Field> fields) {
        final String prefix = fields == null || fields.isEmpty() ? " <absent>" : L_DELIMITER;
        final StringJoiner fieldsInfo = new StringJoiner(L_DELIMITER, prefix, "");
        if (fields != null) {
            fields.forEach(field -> fieldsInfo.add(value(this::getFieldInfo, field)));
        }
        this.additionalInfo.add("    Fields:" + fieldsInfo);
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param field nullable {@link Field}
     * @return this
     */
    public ExceptionBuilder<E> field(final Field field) {
        this.additionalInfo.add("    Field: " + value(this::getFieldInfo, field));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param object nullable object
     * @return this
     */
    public ExceptionBuilder<E> value(final Object object) {
        this.additionalInfo.add("    Value: " + value(String::valueOf, object));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param object nullable object
     * @return this
     */
    public ExceptionBuilder<E> valueType(final Object object) {
        return valueType(value(Object::getClass, object));
    }

    /**
     * Add additional info to Exception message
     *
     * @param type nullable {@link Type}
     * @return this
     */
    public ExceptionBuilder<E> valueType(final Type type) {
        this.additionalInfo.add("    Value type: " + value(Type::getTypeName, type));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param actual nullable object
     * @return this
     */
    public ExceptionBuilder<E> actual(final Object actual) {
        this.additionalInfo.add("    Actual: " + value(String::valueOf, actual));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param object nullable object
     * @return this
     */
    public ExceptionBuilder<E> actualValue(final Object object) {
        this.additionalInfo.add("    Actual value: " + value(String::valueOf, object));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param fieldName nullable String
     * @return this
     */
    public ExceptionBuilder<E> sourceField(final String fieldName) {
        this.additionalInfo.add("    Source field: " + fieldName);
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param sourceValue nullable object
     * @return this
     */
    public ExceptionBuilder<E> sourceValue(final Object sourceValue) {
        if (sourceValue == null) {
            this.additionalInfo.add("    Source value: null");
        } else {
            final Class<?> aClass = value(Object::getClass, sourceValue);
            final boolean isArray = value(Class::isArray, aClass);
            final String value = (isArray ? Arrays.toString((Object[]) sourceValue) : String.valueOf(sourceValue));
            this.additionalInfo.add("    Source value: " + value);
        }
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param field nullable {@link Field}
     * @return this
     */
    public ExceptionBuilder<E> targetField(final Field field) {
        final String value = value(this::getFieldInfo, field);
        this.additionalInfo.add("    Target field: " + value);
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param exception nullable {@link Exception}
     * @return this
     */
    public ExceptionBuilder<E> errorCause(final Exception exception) {
        final List<Throwable> causes = getNestedCauses(exception);
        final String message;
        if (causes.isEmpty()) {
            message = "<absent>";
        } else if (causes.size() == 1) {
            message = causes.get(0).getMessage();
        } else {
            final StringJoiner stringJoiner = new StringJoiner(L_DELIMITER, L_DELIMITER, "");
            causes.forEach(c -> stringJoiner.add(value(Throwable::getMessage, c)));
            message = stringJoiner.toString();
        }
        this.additionalInfo.add("    Error cause: " + message);
        this.cause = exception;
        return this;
    }

    /**
     * @return built exception message
     */
    public String getMessage() {
        return "\n  " + getErrorMessage() + getAdditionalInfo().add("");
    }

    /**
     * @return nullable {@link Exception} cause
     */
    public Exception getCause() {
        return cause;
    }

    /**
     * @return error message without additional info
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return additional info without error message
     */
    public StringJoiner getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * @param function any function
     * @param rawValue nullable function incoming parameter value
     * @param <T>      function incoming parameter type
     * @param <R>      function return type
     * @return function result or null if rawValue is null
     */
    protected <T, R> R value(final Function<T, R> function, final T rawValue) {
        return rawValue == null ? null : function.apply(rawValue);
    }

    /**
     * @param field {@link Field}
     * @return field info in format {@code private String foo;}
     */
    protected String getFieldInfo(final Field field) {
        final int mod = field.getModifiers();
        return ((mod == 0) ? "" : (Modifier.toString(mod) + " "))
               + field.getType().getSimpleName() + " "
               + field.getName() + ";";
    }

    /**
     * Collect nested exception causes
     *
     * @param throwable nullable error
     * @return List of nested exception causes
     */
    protected List<Throwable> getNestedCauses(final Throwable throwable) {
        final List<Throwable> result = new ArrayList<>();
        if (throwable != null) {
            result.add(throwable);
            if (throwable.getCause() != null) {
                result.addAll(getNestedCauses(throwable.getCause()));
            }
        }
        return result;
    }

}
