package qa.model;

import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;

import java.util.Map;

@FormUrlEncoded
public class AdditionalPropertiesStringInteger {

    @FormUrlEncodedAdditionalProperties()
    private Map<String, Integer> additionalProperties;

}