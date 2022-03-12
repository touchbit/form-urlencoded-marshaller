package qa.model;

import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;

import java.util.Map;

@FormUrlEncoded
public class AdditionalPropertiesIntegerString {

    @FormUrlEncodedAdditionalProperties()
    private Map<Integer, String> additionalProperties;

}
