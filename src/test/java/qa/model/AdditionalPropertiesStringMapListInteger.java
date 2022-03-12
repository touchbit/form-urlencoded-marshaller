package qa.model;

import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;

import java.util.List;
import java.util.Map;

@FormUrlEncoded
public class AdditionalPropertiesStringMapListInteger {

    @FormUrlEncodedAdditionalProperties()
    private Map<String, Map<String, List<Integer>>> additionalProperties;

}