package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
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

    private static final String CONSTANT_C = "constant";
    @FormUrlEncodedField(CONSTANT_C)
    public static final Pojo ANNOTATED_CONSTANT = new Pojo();

    private static final String NESTED_POJO_C = "nestedPojo";
    @FormUrlEncodedField(NESTED_POJO_C)
    private Pojo nestedPojo;

    private static final String TRANSIENT_POJO_C = "transientPojo";
    @FormUrlEncodedField(TRANSIENT_POJO_C)
    private transient Pojo transientPojo;

    @FormUrlEncodedField("")
    private String emptyFormUrlEncodedField;

    private static final String MISSED_C = "missed";
    @FormUrlEncodedField(MISSED_C)
    private String missed;

    private static final String STRING_C = "string";
    @FormUrlEncodedField(STRING_C)
    private String string;

    private static final String INTEGER_C = "integer";
    @FormUrlEncodedField(INTEGER_C)
    private Integer integer;

    private static final String STRING_F_C = "stringF";
    @FormUrlEncodedField(STRING_F_C)
    private String stringF;
    private static final String BOOLEAN_F_C = "booleanF";
    @FormUrlEncodedField(BOOLEAN_F_C)
    private Boolean booleanF;
    private static final String SHORT_F_C = "shortF";
    @FormUrlEncodedField(SHORT_F_C)
    private Short shortF;
    private static final String LONG_F_C = "longF";
    @FormUrlEncodedField(LONG_F_C)
    private Long longF;
    private static final String FLOAT_F_C = "floatF";
    @FormUrlEncodedField(FLOAT_F_C)
    private Float floatF;
    private static final String INTEGER_F_C = "integerF";
    @FormUrlEncodedField(INTEGER_F_C)
    private Integer integerF;
    private static final String DOUBLE_F_C = "doubleF";
    @FormUrlEncodedField(DOUBLE_F_C)
    private Double doubleF;
    private static final String BIG_INTEGER_F_C = "bigIntegerF";
    @FormUrlEncodedField(BIG_INTEGER_F_C)
    private BigInteger bigIntegerF;
    private static final String BIG_DECIMAL_F_C = "bigDecimalF";
    @FormUrlEncodedField(BIG_DECIMAL_F_C)
    private BigDecimal bigDecimalF;

    private static final String OBJECT_C = "object";
    @FormUrlEncodedField(OBJECT_C)
    private Object object;

    private static final String ARRAY_STRING_C = "arrayString";
    @FormUrlEncodedField(ARRAY_STRING_C)
    private String[] arrayString;

    private static final String ARRAY_INTEGER_C = "arrayInteger";
    @FormUrlEncodedField(ARRAY_INTEGER_C)
    private Integer[] arrayInteger;

    private static final String ARRAY_MAP_OBJECT_C = "arrayMapObject";
    @FormUrlEncodedField(ARRAY_MAP_OBJECT_C)
    private Map<String, Object>[] arrayMapObject;

    private static final String ARRAY_LIST_OBJECT_C = "arrayListObject";
    @FormUrlEncodedField(ARRAY_LIST_OBJECT_C)
    private List<Object>[] arrayListObject;

    private static final String ARRAY_RAW_MAP_C = "arrayRawMap";
    @FormUrlEncodedField(ARRAY_RAW_MAP_C)
    private Map[] arrayRawMap;

    private static final String ARRAY_POJO_C = "arrayPojo";
    @FormUrlEncodedField(ARRAY_POJO_C)
    private Pojo[] arrayPojo;

    private static final String LIST_RAW_C = "listRaw";
    @FormUrlEncodedField(LIST_RAW_C)
    private List listRaw;

    private static final String LIST_RAW_GENERIC_C = "listRawGeneric";
    @FormUrlEncodedField(LIST_RAW_GENERIC_C)
    private List<?> listRawGeneric;

    private static final String LIST_STRING_C = "listString";
    @FormUrlEncodedField(LIST_STRING_C)
    private List<String> listString;

    private static final String LIST_LIST_STRING_C = "listListString";
    @FormUrlEncodedField(LIST_LIST_STRING_C)
    private List<List<String>> listListString;

    private static final String LIST_LIST_POJO_C = "listListPojo";
    @FormUrlEncodedField(LIST_LIST_POJO_C)
    private List<List<Pojo>> listListPojo;

    private static final String LIST_POJO_C = "listPojo";
    @FormUrlEncodedField(LIST_POJO_C)
    private List<Pojo> listPojo;

    private static final String LIST_OBJECT_C = "listObject";
    @FormUrlEncodedField(LIST_OBJECT_C)
    private List<Object> listObject;

    private static final String LIST_INTEGER_C = "listInteger";
    @FormUrlEncodedField(LIST_INTEGER_C)
    private List<Integer> listInteger;

    private static final String LIST_LIST_INTEGER_C = "listListInteger";
    @FormUrlEncodedField(LIST_LIST_INTEGER_C)
    private List<List<Integer>> listListInteger;

    private static final String LIST_ARRAY_INTEGER_C = "listArrayInteger";
    @FormUrlEncodedField(LIST_ARRAY_INTEGER_C)
    private List<Integer[]> listArrayInteger;

    private static final String LIST_MAP_STRING_INTEGER_C = "listMapStringInteger";
    @FormUrlEncodedField(LIST_MAP_STRING_INTEGER_C)
    private List<Map<String, Integer>> listMapStringInteger;

    private static final String MAP_OBJECT_C = "mapObject";
    @FormUrlEncodedField(MAP_OBJECT_C)
    private Map<String, Object> mapObject;

    private static final String MAP_POJO_C = "mapPojo";
    @FormUrlEncodedField(MAP_POJO_C)
    private Map<String, Pojo> mapPojo;

    private static final String MAP_MAP_STRING_C = "mapMapString";
    @FormUrlEncodedField(MAP_MAP_STRING_C)
    private Map<String, Map<String, String>> mapMapString;

    private static final String MAP_MAP_INTEGER_C = "mapMapInteger";
    @FormUrlEncodedField(MAP_MAP_INTEGER_C)
    private Map<String, Map<String, Integer>> mapMapInteger;

    private static final String MAP_RAW_GENERIC_C = "mapRawGeneric";
    @FormUrlEncodedField(MAP_RAW_GENERIC_C)
    private Map<?, ?> mapRawGeneric;

    private static final String MAP_RAW_C = "mapRaw";
    @FormUrlEncodedField(MAP_RAW_C)
    private Map mapRaw;

    @FormUrlEncodedAdditionalProperties()
    private Map<String, Object> additionalProperties;

    public static String getTypeName() {
        return Pojo.class.getTypeName();
    }

    public enum PojoFields {
        CONSTANT(CONSTANT_C),
        NESTED_POJO(NESTED_POJO_C),
        MISSED(MISSED_C),
        STRING(STRING_C),
        INTEGER(INTEGER_C),
        OBJECT(OBJECT_C),
        ARRAY_STRING(ARRAY_STRING_C),
        ARRAY_INTEGER(ARRAY_INTEGER_C),
        ARRAY_MAP_OBJECT(ARRAY_MAP_OBJECT_C),
        ARRAY_LIST_OBJECT(ARRAY_LIST_OBJECT_C),
        ARRAY_RAW_MAP(ARRAY_RAW_MAP_C),
        ARRAY_POJO(ARRAY_POJO_C),
        LIST_RAW(LIST_RAW_C),
        LIST_RAW_GENERIC(LIST_RAW_GENERIC_C),
        LIST_STRING(LIST_STRING_C),
        LIST_LIST_STRING(LIST_LIST_STRING_C),
        LIST_LIST_POJO(LIST_LIST_POJO_C),
        LIST_POJO(LIST_POJO_C),
        LIST_OBJECT(LIST_OBJECT_C),
        LIST_INTEGER(LIST_INTEGER_C),
        LIST_LIST_INTEGER(LIST_LIST_INTEGER_C),
        LIST_ARRAY_INTEGER(LIST_ARRAY_INTEGER_C),
        LIST_MAP_STRING_INTEGER(LIST_MAP_STRING_INTEGER_C),
        MAP_OBJECT(MAP_OBJECT_C),
        MAP_POJO(MAP_POJO_C),
        MAP_MAP_STRING(MAP_MAP_STRING_C),
        MAP_MAP_INTEGER(MAP_MAP_INTEGER_C),
        MAP_RAW_GENERIC(MAP_RAW_GENERIC_C),
        MAP_RAW(MAP_RAW_C),
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
