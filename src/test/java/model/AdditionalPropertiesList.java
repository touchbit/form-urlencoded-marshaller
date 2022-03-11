package model;

import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;

import java.util.List;

@FormUrlEncoded
public class AdditionalPropertiesList {

    @FormUrlEncodedAdditionalProperties()
    private List<String> additionalProperties;

}
