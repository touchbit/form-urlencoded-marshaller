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
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

public abstract class ExceptionBuilder<E extends RuntimeException> {

    private String errorMessage = "\n";
    private Exception cause = null;
    private final StringJoiner additionalInfo = new StringJoiner("\n");
    protected static final String L_DELIMITER = "\n     - ";

    public abstract E build();

    public ExceptionBuilder<E> errorMessage(final String errorMessage) {
        this.errorMessage = errorMessage + "\n";
        return this;
    }

    public ExceptionBuilder<E> targetType(final Type targetType) {
        this.additionalInfo.add("    Target type: " + value(Type::getTypeName, targetType));
        return this;
    }

    public ExceptionBuilder<E> sourceType(final Object object) {
        final Class<?> aClass = value(Object::getClass, object);
        return sourceType(aClass);
    }

    public ExceptionBuilder<E> sourceType(final Type sourceType) {
        this.additionalInfo.add("    Source type: " + value(Type::getTypeName, sourceType));
        return this;
    }

    public ExceptionBuilder<E> actualType(final Object object) {
        return object == null ? sourceType(null) : sourceType(object.getClass());
    }

    public ExceptionBuilder<E> actualType(final Type actualType) {
        this.additionalInfo.add("    Actual type: " + value(Type::getTypeName, actualType));
        return this;
    }

    public ExceptionBuilder<E> expectedType(Type expectedType) {
        this.additionalInfo.add("    Expected type: " + value(Type::getTypeName, expectedType));
        return this;
    }

    public ExceptionBuilder<E> expectedValue(Object expectedValue) {
        this.additionalInfo.add("    Expected value: " + value(String::valueOf, expectedValue));
        return this;
    }

    public ExceptionBuilder<E> source(Object object) {
        this.additionalInfo.add("    Source: " + value(String::valueOf, object));
        return this;
    }

    public ExceptionBuilder<E> expected(Object object) {
        this.additionalInfo.add("    Expected: " + value(String::valueOf, object));
        return this;
    }

    public ExceptionBuilder<E> expectedHeirsOf(Type type) {
        this.additionalInfo.add("    Expected: heirs of " + value(Type::getTypeName, type));
        return this;
    }

    public ExceptionBuilder<E> model(Type type) {
        this.additionalInfo.add("    Model: " + value(Type::getTypeName, type));
        return this;
    }

    public ExceptionBuilder<E> annotation(Class<?> type) {
        this.additionalInfo.add("    Annotation: @" + value(Class::getSimpleName, type));
        return this;
    }

    public ExceptionBuilder<E> annotationType(Type type) {
        this.additionalInfo.add("    Annotation type: " + value(Type::getTypeName, type));
        return this;
    }

    public ExceptionBuilder<E> fields(List<Field> fields) {
        final StringJoiner fieldsInfo = new StringJoiner(L_DELIMITER, fields.isEmpty() ? "[]" : L_DELIMITER, "");
        fields.forEach(field -> fieldsInfo.add(value(this::getFieldInfo, field)));
        this.additionalInfo.add("    Fields:" + fieldsInfo);
        return this;
    }

    public ExceptionBuilder<E> field(Field field) {
        this.additionalInfo.add("    Field: " + value(this::getFieldInfo, field));
        return this;
    }

    public ExceptionBuilder<E> actual(Object actual) {
        this.additionalInfo.add("    Actual: " + value(String::valueOf, actual));
        return this;
    }

    public ExceptionBuilder<E> actualValue(Object actualValue) {
        this.additionalInfo.add("    Actual value: " + value(String::valueOf, actualValue));
        return this;
    }

    public ExceptionBuilder<E> sourceField(String fieldName) {
        this.additionalInfo.add("    Source field: " + fieldName);
        return this;
    }

    public ExceptionBuilder<E> sourceValue(final Object sourceValue) {
        final Class<?> aClass = value(Object::getClass, sourceValue);
        final boolean isArray = value(Class::isArray, aClass);
        final String value = (isArray ? Arrays.toString((Object[]) sourceValue) : String.valueOf(sourceValue));
        this.additionalInfo.add("    Source value: " + value);
        return this;
    }

    public ExceptionBuilder<E> targetField(Field field) {
        final String value = value(this::getFieldInfo, field);
        this.additionalInfo.add("    Target field: " + value);
        return this;
    }

    public ExceptionBuilder<E> errorCause(final Exception e) {
        this.additionalInfo.add("    Error cause: " + e.getMessage().trim());
        this.cause = e;
        return this;
    }

    public String getMessage() {
        return "\n  " + getErrorMessage() + getAdditionalInfo().add("");
    }

    public Exception getCause() {
        return cause;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public StringJoiner getAdditionalInfo() {
        return additionalInfo;
    }

    protected <T, R> R value(Function<T, R> function, T rawValue) {
        return rawValue == null ? null : function.apply(rawValue);
    }

    protected String getFieldInfo(final Field field) {
        final int mod = field.getModifiers();
        return ((mod == 0) ? "" : (Modifier.toString(mod) + " "))
               + field.getType().getSimpleName() + " "
               + field.getName() + ";";
    }

}
