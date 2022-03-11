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

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 11.03.2022
 */
public class ChainException extends MarshallerException {

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to initCause.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public ChainException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * Note that the detail message associated with cause is not automatically
     * incorporated in this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param t       the cause (which is saved for later retrieval by the getCause() method).
     *                (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ChainException(String message, Throwable t) {
        super(message, t);
    }

    /**
     * @return instance of {@link ExceptionBuilder}
     */
    @SuppressWarnings("java:S1452") // Generic wildcard does not affect anything
    public static ExceptionBuilder<?> builder() {
        return new ExceptionBuilder<ChainException>() {
            @Override
            public ChainException build() {
                return new ChainException(getMessage(), getCause());
            }
        };
    }

}
