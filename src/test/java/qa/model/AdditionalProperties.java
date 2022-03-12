package qa.model;

import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;

import java.util.Map;

@FormUrlEncoded
public class AdditionalProperties {

    @FormUrlEncodedAdditionalProperties()
    public Map<String, Object> additionalProperties;

}
