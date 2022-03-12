package qa.model;

import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;

import java.util.Map;

@FormUrlEncoded
public class AdditionalPropertiesSeveralFields {

    @FormUrlEncodedAdditionalProperties()
    private Map<String, Object> additionalProperties1;

    @FormUrlEncodedAdditionalProperties()
    private Map<String, Object> additionalProperties2;

}
