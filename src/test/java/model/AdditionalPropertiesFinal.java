package model;

import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;

import java.util.HashMap;
import java.util.Map;

@FormUrlEncoded
public class AdditionalPropertiesFinal {

    @FormUrlEncodedAdditionalProperties()
    public final Map<String, Object> additionalProperties = new HashMap<>();

}
