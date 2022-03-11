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
 * Created: 18.02.2022
 */
public class MarshallerException extends RuntimeException {

    public MarshallerException(String message) {
        super(message);
    }

    public MarshallerException(String message, Throwable t) {
        super(message, t);
    }

    @SuppressWarnings("java:S1452") // Generic wildcard does not affect anything
    public static ExceptionBuilder<?> builder() {
        return new ExceptionBuilder<MarshallerException>() {
            @Override
            public MarshallerException build() {
                return new MarshallerException(getMessage(), getCause());
            }
        };
    }

}
