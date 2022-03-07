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

package org.touchbit.www.form.urlencoded.marshaller.chain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * interface for working with from url encoded array
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 04.03.2022
 */
public interface IChainList extends List<Object> {

    /**
     * @return sign that from array is not indexed
     */
    boolean isNotIndexed();

    /**
     * @return sign that indexed form array is not filled (has nullable values)
     */
    boolean isNotFilled();

    /**
     * {@link ArrayList} for working with form array
     * <p>
     *
     * @author Oleg Shaburov (shaburov.o.a@gmail.com)
     * Created: 04.03.2022
     */
    class Default extends ArrayList<Object> implements IChainList {

        /**
         * sign that form array is indexed
         */
        private final boolean isIndexed;

        /**
         * @param isIndexed - sign that form array is indexed
         */
        public Default(final boolean isIndexed) {
            this.isIndexed = isIndexed;
        }

        /**
         * @return sign that form array is not indexed
         */
        @Override
        public boolean isNotIndexed() {
            return !isIndexed;
        }

        /**
         * @return sign that indexed form array is not filled (has nullable values)
         */
        @Override
        public boolean isNotFilled() {
            return !isIndexed || this.stream().anyMatch(Objects::isNull);
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

    }
}
