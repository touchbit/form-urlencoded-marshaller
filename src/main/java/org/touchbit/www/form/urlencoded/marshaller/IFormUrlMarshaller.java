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

import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;

/**
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 19.02.2022
 * @see FormUrlEncoded
 * @see FormUrlEncodedField
 * @see FormUrlEncodedAdditionalProperties
 */
public interface IFormUrlMarshaller {

    /**
     * @return current marshaller configuration
     */
    IFormUrlMarshallerConfiguration getConfiguration();

    /**
     * Model to string conversion
     *
     * @param model FormUrlEncoded model
     * @return model string representation
     */
    String marshal(Object model);

    /**
     * String to model conversion
     *
     * @param modelClass    FormUrlEncoded model class
     * @param encodedString String data to conversation
     * @param <M>           FormUrlEncoded model type
     * @return completed model
     */
    <M> M unmarshal(Class<M> modelClass, String encodedString);

}
