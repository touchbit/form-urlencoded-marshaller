package qa.model;

import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;

@FormUrlEncoded
@SuppressWarnings("FieldCanBeLocal")
public class PojoWithConstructorParams {

    private final String foo;

    public PojoWithConstructorParams(String foo) {
        this.foo = foo;
    }

}
