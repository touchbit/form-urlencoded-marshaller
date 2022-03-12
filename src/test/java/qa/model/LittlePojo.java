package qa.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;

@Getter
@Setter
@ToString
@Accessors(chain = true, fluent = true)
@FormUrlEncoded
public class LittlePojo {

    @FormUrlEncodedField("foo")
    private String foo;

    @FormUrlEncodedField(value = "bar", encoded = true)
    private String barEncoded;

}
