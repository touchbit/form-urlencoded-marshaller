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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("UnusedReturnValue")
public interface IFormUrlMarshallerConfiguration {

    /**
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @return URL form data coding charset
     */
    Charset getCodingCharset();

    /**
     * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
     *
     * @param codingCharset URL form data coding charset
     * @return this
     */
    IFormUrlMarshallerConfiguration withCodingCharset(final Charset codingCharset);

    /**
     * @return true if form-urlencoded pretty print enabled
     */
    boolean isPrettyPrint();

    /**
     * @param isPrettyPrint form-urlencoded pretty print flag
     * @return this
     */
    IFormUrlMarshallerConfiguration withPrettyPrint(final boolean isPrettyPrint);

    /**
     * Enable hidden array format: {@code foo=100&foo=200...&foo=100500}
     *
     * @return this
     */
    IFormUrlMarshallerConfiguration enableHiddenList();

    /**
     * Enable non-indexed array format: {@code foo[]=100&foo[]=200...&foo[]=100500}
     *
     * @return this
     */
    IFormUrlMarshallerConfiguration enableImplicitList();

    /**
     * Enable indexed array format: {@code foo[0]=100&foo[1]=200...&foo[n]=100500}
     *
     * @return this
     */
    IFormUrlMarshallerConfiguration enableExplicitList();

    /**
     * @return true if hidden array format enabled: {@code foo=100&foo=200...&foo=100500}
     */
    boolean isHiddenList();

    /**
     * @return true if non-indexed array format enabled: {@code foo[]=100&foo[]=200...&foo[]=100500}
     */
    boolean isImplicitList();

    /**
     * @return true if indexed array format enabled: {@code foo[0]=100&foo[1]=200...&foo[n]=100500}
     */
    boolean isExplicitList();

    class Default implements IFormUrlMarshallerConfiguration {

        /**
         * URL form data coding charset
         * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
         */
        private Charset codingCharset = StandardCharsets.UTF_8;
        private boolean isImplicitList = false;
        private boolean isExplicitList = false;
        private boolean isPrettyPrint = true;

        /**
         * Enable hidden array format: {@code foo=100&foo=200...&foo=100500}
         *
         * @return this
         */
        public Default enableHiddenList() {
            this.isImplicitList = false;
            this.isExplicitList = false;
            return this;
        }

        /**
         * Enable non-indexed array format: {@code foo[]=100&foo[]=200...&foo[]=100500}
         *
         * @return this
         */
        public Default enableImplicitList() {
            this.isImplicitList = true;
            this.isExplicitList = false;
            return this;
        }

        /**
         * Enable indexed array format: {@code foo[0]=100&foo[1]=200...&foo[n]=100500}
         *
         * @return this
         */
        public Default enableExplicitList() {
            this.isImplicitList = false;
            this.isExplicitList = true;
            return this;
        }

        /**
         * @return true if non-indexed array format enabled: {@code foo[]=100&foo[]=200...&foo[]=100500}
         */
        public boolean isImplicitList() {
            return isImplicitList;
        }

        /**
         * @return true if indexed array format enabled: {@code foo[0]=100&foo[1]=200...&foo[n]=100500}
         */
        public boolean isExplicitList() {
            return isExplicitList;
        }

        /**
         * @return true if hidden array format enabled: {@code foo=100&foo=200...&foo=100500}
         */
        public boolean isHiddenList() {
            return !isImplicitList() && !isExplicitList();
        }

        /**
         * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
         *
         * @return URL form data coding charset
         */
        @Override
        public Charset getCodingCharset() {
            return this.codingCharset;
        }

        /**
         * According to the 3W specification, it is strongly recommended to use UTF-8 charset for URL form data coding.
         *
         * @param codingCharset URL form data coding charset
         * @return this
         */
        public Default withCodingCharset(final Charset codingCharset) {
            this.codingCharset = codingCharset;
            return this;
        }

        /**
         * @return true if form-urlencoded pretty print enabled
         */
        @Override
        public boolean isPrettyPrint() {
            return isPrettyPrint;
        }

        /**
         * @param isPrettyPrint form-urlencoded pretty print flag
         * @return this
         */
        @Override
        public IFormUrlMarshallerConfiguration withPrettyPrint(boolean isPrettyPrint) {
            this.isPrettyPrint = isPrettyPrint;
            return this;
        }

    }

}
