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

package org.touchbit.www.form.url.codec;

public class CodecConstant {

    public static final String MODEL_PARAMETER = "model";
    public static final String FIELD_PARAMETER = "field";
    public static final String TYPE_PARAMETER = "type";
    public static final String MODEL_CLASS_PARAMETER = "modelClass";
    public static final String ENCODED_STRING_PARAMETER = "encodedString";
    public static final String CODING_CHARSET_PARAMETER = "codingCharset";
    public static final String FORM_FIELD_NAME_PARAMETER = "formFieldName";
    public static final String PARSED_PARAMETER = "parsed";
    public static final String HANDLED_PARAMETER = "handled";
    public static final String VALUE_PARAMETER = "value";
    public static final String FIELD_TYPE_PARAMETER = "fieldType";
    public static final String TARGET_TYPE_PARAMETER = "targetType";
    public static final String PARAMETERIZED_TYPE_PARAMETER = "parameterizedType";
    public static final String KEY_PARAMETER = "key";
    public static final String RAW_DATA_PARAMETER = "rawData";
    public static final String TARGET_PARAMETER = "target";
    public static final String SOURCE_PARAMETER = "source";
    public static final String CHAIN_PART_PARAMETER = "chainPart";
    public static final String LIST_PARAMETER = "list";

    /**
     * Utility class. Forbidden instantiation.
     */
    private CodecConstant() {
        throw new UnsupportedOperationException();
    }

}
