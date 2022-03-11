package model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@FormUrlEncoded
public class MapPojo extends HashMap<String, Object> {

    public static final MapPojo CONSTANT = new MapPojo();

    @FormUrlEncodedField("constant")
    public static final MapPojo ANNOTATED_CONSTANT = new MapPojo();

    @FormUrlEncodedField("nestedMapPojo")
    private MapPojo nestedMapPojo;

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

    @FormUrlEncodedField("listRawField")
    private List listRawField;

    @FormUrlEncodedField("listStringField")
    private List<String> listStringField;

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

    @FormUrlEncodedField("mapMapString")
    private Map<String, Map<String, String>> mapMapString;

    @FormUrlEncodedField("mapMapInteger")
    private Map<String, Map<String, Integer>> mapMapInteger;

    @FormUrlEncodedAdditionalProperties()
    private Map<String, Object> additionalProperties;

    @Override
    public String toString() {
        return "toString";
    }

    public Map<String, Object> toMap() {
        final Map<String, Object> result = new HashMap<>();
        if (nestedMapPojo != null) result.put("nestedMapPojo", nestedMapPojo);
        if (missed != null) result.put("missed", missed);
        if (stringField != null) result.put("stringField", stringField);
        if (integerField != null) result.put("integerField", integerField);
        if (objectField != null) result.put("objectField", objectField);
        if (arrayStringField != null) result.put("arrayStringField", arrayStringField);
        if (arrayIntegerField != null) result.put("arrayIntegerField", arrayIntegerField);
        if (listStringField != null) result.put("listStringField", listStringField);
        if (listObjectField != null) result.put("listObjectField", listObjectField);
        if (listIntegerField != null) result.put("listIntegerField", listIntegerField);
        if (mapObjectField != null) result.put("mapObjectField", mapObjectField);
        return result;
    }

    public enum PojoFields {
        NESTED_MAP_POJO("nestedMapPojo"),
        MISSED("missed"),
        STRING_FIELD("stringField"),
        INTEGER_FIELD("integerField"),
        OBJECT_FIELD("objectField"),
        ARRAY_STRING_FIELD("arrayStringField"),
        ARRAY_INTEGER_FIELD("arrayIntegerField"),
        ARRAY_MAP_OBJECT_FIELD("arrayMapObjectField"),
        ARRAY_RAW_MAP_FIELD("arrayRawMapField"),
        LIST_RAW_FIELD("listRawField"),
        LIST_STRING_FIELD("listStringField"),
        LIST_OBJECT_FIELD("listObjectField"),
        LIST_INTEGER_FIELD("listIntegerField"),
        LIST_LIST_INTEGER_FIELD("listListIntegerField"),
        LIST_ARRAY_INTEGER_FIELD("listArrayIntegerField"),
        LIST_MAP_STRING_INTEGER_FIELD("listMapStringIntegerField"),
        MAP_OBJECT_FIELD("mapObjectField"),
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
                return MapPojo.class.getDeclaredField(this.fieldName);
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
