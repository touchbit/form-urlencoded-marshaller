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

import org.apache.commons.lang3.math.NumberUtils;
import org.touchbit.www.form.urlencoded.marshaller.util.CodecConstant;
import org.touchbit.www.form.urlencoded.marshaller.util.FormUrlUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Representing a single from url key in {@link IChainPart} keychain
 * For example:
 * - {@link IChainPart} - foo[0][bar]=value
 * - {@link IChainKey} - for map - foo or bar
 * - {@link IChainKey} - for array - 0 or empty for implicit array []
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 05.03.2022
 */
interface IChainKey {

    /**
     * @return index for explicit array key or null
     */
    Integer getIndex();

    /**
     * @return from url parameter key type ({@link Map} or {@link List})
     */
    Type getType();

    /**
     * @return true if key is an explicit array
     */
    boolean isIndexedList();

    /**
     * @return true if from parameter key type is an array
     */
    boolean isList();

    /**
     * @return true if from parameter key type is a map
     */
    boolean isMap();

    /**
     * @return key name or null for array
     */
    String getKeyName();

    /**
     * Default {@link IChainKey} implementation
     */
    class Default implements IChainKey {

        private final String keyName;
        private final Integer index;
        private final Type type;

        /**
         * @param key - form parameter key (map key || array index || empty string for implicit array)
         * @throws NullPointerException if key is null
         */
        public Default(final String key) {
            FormUrlUtils.parameterRequireNonNull(key, CodecConstant.KEY_PARAMETER);
            if (key.isEmpty() || NumberUtils.isDigits(key)) {
                this.type = List.class;
                this.keyName = null;
                if (NumberUtils.isDigits(key)) {
                    this.index = Integer.valueOf(key);
                } else {
                    this.index = null;
                }
            } else {
                this.type = Map.class;
                this.index = null;
                this.keyName = key;
            }
        }

        /**
         * @see IChainKey#getIndex()
         */
        public Integer getIndex() {
            return index;
        }

        /**
         * @see IChainKey#getType()
         */
        public Type getType() {
            return type;
        }

        /**
         * @see IChainKey#isIndexedList()
         */
        public boolean isIndexedList() {
            return isList() && index != null;
        }

        /**
         * @see IChainKey#isList()
         */
        public boolean isList() {
            return type.equals(List.class);
        }

        /**
         * @see IChainKey#isMap()
         */
        public boolean isMap() {
            return type.equals(Map.class);
        }

        /**
         * @see IChainKey#getKeyName()
         */
        public String getKeyName() {
            return keyName;
        }

        @Override
        public String toString() {
            if (isList()) {
                if (index == null) {
                    return "L";
                } else {
                    return index + "-L";
                }
            }
            return keyName + "-M";
        }
    }
}
