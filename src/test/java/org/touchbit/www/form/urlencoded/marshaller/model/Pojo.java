package org.touchbit.www.form.urlencoded.marshaller.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Getter
@Setter
@ToString
@Accessors(chain = true, fluent = true)
@FormUrlEncoded
public class Pojo {

    public static final Pojo CONSTANT = new Pojo();

    @FormUrlEncodedField("constant")
    public static final Pojo ANNOTATED_CONSTANT = new Pojo();

    @FormUrlEncodedField("nestedPojo")
    private Pojo nestedPojo;

    @FormUrlEncodedField("missed")
    private String missed;

    @FormUrlEncodedField("stringField")
    private String stringField;

    @FormUrlEncodedField("integerField")
    private Integer integerField;

    @FormUrlEncodedField("objectField")
    private Object objectField;

    @FormUrlEncodedField("arrayStringField")
    private String[] arrayStringField;

    @FormUrlEncodedField("arrayIntegerField")
    private Integer[] arrayIntegerField;

    @FormUrlEncodedField("arrayMapObjectField")
    private Map<String, Object>[] arrayMapObjectField;

    @FormUrlEncodedField("arrayRawMapField")
    private Map[] arrayRawMapField;

    @FormUrlEncodedField("arrayPojoField")
    private Pojo[] arrayPojoField;

    @FormUrlEncodedField("listRawField")
    private List listRawField;

    @FormUrlEncodedField("listStringField")
    private List<String> listStringField;

    @FormUrlEncodedField("listPojoField")
    private List<Pojo> listPojoField;

    @FormUrlEncodedField("listObjectField")
    private List<Object> listObjectField;

    @FormUrlEncodedField("listIntegerField")
    private List<Integer> listIntegerField;

    @FormUrlEncodedField("listListIntegerField")
    private List<List<Integer>> listListIntegerField;

    @FormUrlEncodedField("listArrayIntegerField")
    private List<Integer[]> listArrayIntegerField;

    @FormUrlEncodedField("listMapStringIntegerField")
    private List<Map<String, Integer>> listMapStringIntegerField;

    @FormUrlEncodedField("mapObjectField")
    private Map<String, Object> mapObjectField;

    @FormUrlEncodedField("mapPojoField")
    private Map<String, Pojo> mapPojoField;

    @FormUrlEncodedField("mapMapString")
    private Map<String, Map<String, String>> mapMapString;

    @FormUrlEncodedField("mapMapInteger")
    private Map<String, Map<String, Integer>> mapMapInteger;

    @FormUrlEncodedAdditionalProperties()
    private Map<String, Object> additionalProperties;

    public static String getTypeName() {
        return Pojo.class.getTypeName();
    }

    public enum PojoFields {
        NESTED_POJO("nestedPojo"),
        MISSED("missed"),
        STRING_FIELD("stringField"),
        INTEGER_FIELD("integerField"),
        OBJECT_FIELD("objectField"),
        ARRAY_STRING_FIELD("arrayStringField"),
        ARRAY_INTEGER_FIELD("arrayIntegerField"),
        ARRAY_MAP_OBJECT_FIELD("arrayMapObjectField"),
        ARRAY_RAW_MAP_FIELD("arrayRawMapField"),
        ARRAY_POJO_FIELD("arrayPojoField"),
        LIST_RAW_FIELD("listRawField"),
        LIST_POJO_FIELD("listPojoField"),
        LIST_STRING_FIELD("listStringField"),
        LIST_OBJECT_FIELD("listObjectField"),
        LIST_INTEGER_FIELD("listIntegerField"),
        LIST_LIST_INTEGER_FIELD("listListIntegerField"),
        LIST_ARRAY_INTEGER_FIELD("listArrayIntegerField"),
        LIST_MAP_STRING_INTEGER_FIELD("listMapStringIntegerField"),
        MAP_OBJECT_FIELD("mapObjectField"),
        MAP_POJO_FIELD("mapPojoField"),
        MAP_MAP_STRING("mapMapString"),
        MAP_MAP_INTEGER("mapMapInteger"),
        ;

        private final String fieldName;

        PojoFields(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Field getDeclaredField() {
            try {
                return Pojo.class.getDeclaredField(this.fieldName);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Test corrupted", e);
            }
        }

        public Type getGenericType() {
            return getDeclaredField().getGenericType();
        }

        public Type getType() {
            return getDeclaredField().getType();
        }

    }

}
