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

import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 11.03.2022
 */
public class CodecConstant {

    /***/
    public static final String ENCODED_STRING_PARAMETER = "encodedString";
    /***/
    public static final String CODING_CHARSET_PARAMETER = "codingCharset";
    /***/
    public static final String MODEL_CLASS_PARAMETER = "modelClass";
    /***/
    public static final String TARGET_TYPE_PARAMETER = "targetType";
    /***/
    public static final String CHAIN_PART_PARAMETER = "chainPart";
    /***/
    public static final String RAW_DATA_PARAMETER = "rawData";
    /***/
    public static final String A_CLASS_PARAMETER = "aClass";
    /***/
    public static final String TARGET_PARAMETER = "target";
    /***/
    public static final String OBJECT_PARAMETER = "object";
    /***/
    public static final String SOURCE_PARAMETER = "source";
    /***/
    public static final String MODEL_PARAMETER = "model";
    /***/
    public static final String FIELD_PARAMETER = "field";
    /***/
    public static final String VALUE_PARAMETER = "value";
    /***/
    public static final String TYPE_PARAMETER = "type";
    /***/
    public static final String LIST_PARAMETER = "list";
    /***/
    public static final String KEY_PARAMETER = "key";

    /***/
    public static final String ERR_POJO_CLASSES_WITH_FORM_URLENCODED_ANNOTATION = "POJO classes with @" + FormUrlEncoded.class.getSimpleName() + " annotation";
    /***/
    public static final String ERR_SIMPLE_COMPLEX_REFERENCE_TYPE_ARRAY = "simple/complex reference type array (String[], POJO[], etc.)";
    /***/
    public static final String ERR_UNMAPPED_ADDITIONAL_PROPERTIES = "URL encoded string contains unmapped additional properties.";
    /***/
    public static final String ERR_INCOMPATIBLE_TYPES_RECEIVED_FOR_CONVERSION = "Incompatible types received for conversion.";
    /***/
    public static final String ERR_RECEIVED_UNSUPPORTED_TYPE_FOR_CONVERSION = "Received unsupported type for conversion.";
    /***/
    public static final String ERR_SIMPLE_REFERENCE_TYPES = "simple reference types (String, Integer, Boolean, etc.)";
    /***/
    public static final String THERE_ARE_NO_ADDITIONAL_PROPERTIES = "There are no additional properties.";

    /**
     * Utility class. Forbidden instantiation.
     */
    private CodecConstant() {
    }

}
