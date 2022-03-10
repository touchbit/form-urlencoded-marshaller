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
import java.util.StringJoiner;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 18.02.2022
 */
public class MarshallerException extends RuntimeException {

    public MarshallerException(String message) {
        super(message);
    }

    public MarshallerException(String message, Throwable t) {
        super(message, t);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String errorMessage = "\n";
        private final StringJoiner additionalInfo = new StringJoiner("\n");

        public Builder errorMessage(final String errorMessage) {
            this.errorMessage = errorMessage + "\n";
            return this;
        }

        public Builder targetType(final Object object) {
            return object == null ? targetType(null) : targetType(object.getClass());
        }

        public Builder targetType(final Type targetType) {
            final String value;
            if (targetType == null) {
                value = null;
            } else {
                value = targetType.getTypeName();
            }
            this.additionalInfo.add("    Target type: " + value);
            return this;
        }

        public Builder sourceType(final Object object) {
            return object == null ? sourceType(null) : sourceType(object.getClass());
        }

        public Builder sourceType(final Type sourceType) {
            final String value;
            if (sourceType == null) {
                value = null;
            } else {
                value = sourceType.getTypeName();
            }
            this.additionalInfo.add("    Source type: " + value);
            return this;
        }

        public Builder actualType(final Object object) {
            return object == null ? sourceType(null) : sourceType(object.getClass());
        }

        public Builder actualType(final Type actualType) {
            final String value;
            if (actualType == null) {
                value = null;
            } else {
                value = actualType.getTypeName();
            }
            this.additionalInfo.add("    Actual type: " + value);
            return this;
        }

        public Builder expectedType(Type expectedType) {
            final String value;
            if (expectedType == null) {
                value = null;
            } else {
                value = expectedType.getTypeName();
            }
            this.additionalInfo.add("    Expected type: " + value);
            return this;
        }

        public Builder expectedValue(Object expectedValue) {
            final String value;
            if (expectedValue == null) {
                value = null;
            } else {
                value = String.valueOf(expectedValue);
            }
            this.additionalInfo.add("    Expected value: " + value);
            return this;
        }

        public Builder source(Object object) {
            final String value;
            if (object == null) {
                value = null;
            } else {
                value = String.valueOf(object);
            }
            this.additionalInfo.add("    Source: " + value);
            return this;
        }

        public Builder expected(Object object) {
            final String value;
            if (object == null) {
                value = null;
            } else {
                value = String.valueOf(object);
            }
            this.additionalInfo.add("    Expected: " + value);
            return this;
        }

        public Builder actual(Object actual) {
            final String value;
            if (actual == null) {
                value = null;
            } else {
                value = String.valueOf(actual);
            }
            this.additionalInfo.add("    Actual: " + value);
            return this;
        }

        public Builder actualValue(Object actualValue) {
            final String value;
            if (actualValue == null) {
                value = null;
            } else {
                value = String.valueOf(actualValue);
            }
            this.additionalInfo.add("    Actual value: " + value);
            return this;
        }

        public Builder sourceField(String fieldName) {
            this.additionalInfo.add("    Source field: " + fieldName);
            return this;
        }

        public Builder sourceValue(final Object sourceValue) {
            final String value;
            if (sourceValue != null) {
                if (sourceValue.getClass().isArray()) {
                    value = Arrays.toString((Object[]) sourceValue);
                } else {
                    value = String.valueOf(sourceValue);
                }
            } else {
                value = null;
            }
            this.additionalInfo.add("    Source value: " + value);
            return this;
        }

        public Builder targetField(Field field) {
            final String value;
            if (field != null) {
                final int mod = field.getModifiers();
                value = ((mod == 0) ? "" : (Modifier.toString(mod) + " "))
                        + field.getType().getSimpleName() + " "
                        + field.getName() + ";";
            } else {
                value = "null";
            }
            this.additionalInfo.add("    Target field: " + value);
            return this;
        }

        public MarshallerException build() {
            return new MarshallerException(errorMessage + this.additionalInfo.add(""));
        }

    }


}
