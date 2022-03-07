package org.touchbit.www.form.urlencoded.marshaller;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.BaseTest;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FormUrlMarshaller.class unit tests")
public class FormUrlMarshallerUnitTests extends BaseTest {

    private static final String ENCODED = "%D1%82%D0%B5%D1%81%D1%82";
    private static final String DECODED = "тест";

    @Nested
    @DisplayName("#marshal() method tests")
    public class MarshalMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646683219120() {
            assertNPE(() -> marshaller().marshal(null), "model");
        }

        @Test
        @DisplayName("Convert pojo map to form url encoded string")
        public void test1646683248748() {
            final POJO pojo = pojo().pojo(pojo().stringField("string_value"));
            pojo.put("foo", mapOf("bar", 1));
            final FormUrlMarshaller marshaller = marshaller();
            marshaller.getConfiguration().withPrettyPrint(false);
            assertThat(marshaller.marshal(pojo)).isEqualTo("pojo[stringField]=string_value&foo[bar]=1");
        }

        @Test
        @DisplayName("Convert pojo map to form url encoded pretty string")
        public void test1646683633833() {
            final POJO pojo = pojo().pojo(pojo().stringField("string_value"));
            pojo.put("foo", mapOf("bar", 1));
            final FormUrlMarshaller marshaller = marshaller();
            marshaller.getConfiguration().withPrettyPrint(true);
            assertThat(marshaller.marshal(pojo)).isEqualTo("pojo[stringField]=string_value&\nfoo[bar]=1");
        }

        @Test
        @DisplayName("Convert pojo to form url encoded string with implicit list")
        public void test1646683988015() {
            final POJO pojo = pojo().pojo(pojo().stringField("string_value"));
            pojo.put("foo", mapOf("bar", arrayOf(1)));
            final FormUrlMarshaller marshaller = marshaller();
            marshaller.getConfiguration().enableHiddenList().withPrettyPrint(true);
            assertThat(marshaller.marshal(pojo)).isEqualTo("pojo[stringField]=string_value&\nfoo[bar]=1");
        }

        @Test
        @DisplayName("Convert pojo to form url encoded string with implicit list")
        public void test1646683893206() {
            final POJO pojo = pojo().pojo(pojo().stringField("string_value"));
            pojo.put("foo", mapOf("bar", arrayOf(1)));
            final FormUrlMarshaller marshaller = marshaller();
            marshaller.getConfiguration().enableImplicitList().withPrettyPrint(false);
            assertThat(marshaller.marshal(pojo)).isEqualTo("pojo[stringField]=string_value&foo[bar][]=1");
        }

        @Test
        @DisplayName("Convert pojo to form url encoded string with explicit list")
        public void test1646683947701() {
            final POJO pojo = pojo().pojo(pojo().stringField("string_value"));
            pojo.put("foo", mapOf("bar", arrayOf(1)));
            final FormUrlMarshaller marshaller = marshaller();
            marshaller.getConfiguration().enableExplicitList().withPrettyPrint(false);
            assertThat(marshaller.marshal(pojo)).isEqualTo("pojo[stringField]=string_value&foo[bar][0]=1");
        }

        @Test
        @DisplayName("IllegalArgumentException if model type is not supported")
        public void test1646684025352() {
            assertThrow(() -> marshaller().marshal(new Object()))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("Received unsupported type for conversion: class java.lang.Object");
        }
    }

    @Nested
    @DisplayName("#convertValueToRawData() method tests")
    public class ConvertValueToRawDataMethodTests {

        @Test
        @DisplayName("Convert null value to empty string")
        public void test1646682460167() {
            final Object result = marshaller().convertValueToRawData(null);
            assertThat(result).isEqualTo("");
        }

        @Test
        @DisplayName("Convert empty string value to empty string")
        public void test1646682514844() {
            final Object result = marshaller().convertValueToRawData("");
            assertThat(result).isEqualTo("");
        }

        @Test
        @DisplayName("Convert decoded string value to encoded string")
        public void test1646682536460() {
            final Object result = marshaller().convertValueToRawData(DECODED);
            assertThat(result).isEqualTo(ENCODED);
        }

        @Test
        @DisplayName("Convert POJO value to raw data MAP")
        public void test1646682581082() {
            final POJO pojo = pojo().stringField("foo");
            pojo.put("bar", pojo().mapField(mapOf("map_key", null)));
            final Object result = marshaller().convertValueToRawData(pojo);
            assertThat(result.toString()).isEqualTo("{stringField=foo, bar={mapField={map_key=}}}");
        }

        @Test
        @DisplayName("Convert List<POJO> value to raw data ArrayList")
        public void test1646682943678() {
            final Object result = marshaller().convertValueToRawData(listOf(null, pojo()));
            assertThat(result.toString()).isEqualTo("[, {}]");
        }

        @Test
        @DisplayName("Convert POJO[] value to raw data ArrayList")
        public void test1646682985063() {
            final Object result = marshaller().convertValueToRawData(arrayOf(null, pojo()));
            assertThat(result.toString()).isEqualTo("[, {}]");
        }

        @Test
        @DisplayName("IllegalArgumentException if value type is not supported")
        public void test1646683148875() {
            assertThrow(() -> marshaller().convertValueToRawData(new Object()))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("Received unsupported type for conversion: class java.lang.Object");
        }
    }

    @Nested
    @DisplayName("#convertPojoToRawData() method tests")
    public class ConvertPojoToRawDataMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646681756463() {
            assertNPE(() -> marshaller().convertPojoToRawData(null), "value");
        }

        @Test
        @DisplayName("Convert POJO to raw data Map with simple field")
        public void test1646681989639() {
            final POJO pojo = pojo().stringField(DECODED);
            final Map<String, Object> map = marshaller().convertPojoToRawData(pojo);
            assertThat(map.toString()).isEqualTo("{stringField=" + ENCODED + "}");
        }

        @Test
        @DisplayName("Convert POJO to raw data Map with array field")
        public void test1646682111744() {
            final POJO pojo = pojo().stringArrayField(arrayOf("foo"));
            final Map<String, Object> map = marshaller().convertPojoToRawData(pojo);
            assertThat(map.toString()).isEqualTo("{stringArrayField=[foo]}");
        }

        @Test
        @DisplayName("Convert POJO to raw data Map with nested POJO field")
        public void test1646682159512() {
            final POJO pojo = pojo().pojo(pojo().stringField("foo"));
            final Map<String, Object> map = marshaller().convertPojoToRawData(pojo);
            assertThat(map.toString()).isEqualTo("{pojo={stringField=foo}}");
        }

        @Test
        @DisplayName("IllegalArgumentException class does not contain a FormUrlEncodedField annotation")
        public void test1646682319880() {
            assertThrow(() -> marshaller().convertPojoToRawData(new Object()))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("Object class does not contain a required annotation.\n" +
                                     "Class: java.lang.Object\n" +
                                     "Expected annotation: " + FormUrlEncoded.class.getName() + "\n");
        }

    }

    @Nested
    @DisplayName("#convertMapToRawData() method tests")
    public class ConvertMapToRawDataMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646681315876() {
            assertNPE(() -> marshaller().convertMapToRawData(null), "value");
        }

        @Test
        @DisplayName("Convert Map<String, String> to raw data Map<String, Object>")
        public void test1646681342307() {
            final Map<String, Object> map = mapOf("foo", "bar");
            final Map<String, Object> act = marshaller().convertMapToRawData(map);
            assertThat(act).isEqualTo(map);
        }

        @Test
        @DisplayName("Convert Map<String, List<String>> to raw data Map<String, Object>")
        public void test1646681439318() {
            final Map<String, Object> map = mapOf("foo", listOf("bar"));
            final Map<String, Object> act = marshaller().convertMapToRawData(map);
            assertThat(act).isEqualTo(map);
        }

        @Test
        @DisplayName("Convert Map<String, POJO> to raw data Map<String, Object>")
        public void test1646681466555() {
            final Map<String, Object> map = mapOf("foo", pojo());
            final Map<String, Object> act = marshaller().convertMapToRawData(map);
            assertThat(act.toString()).isEqualTo("{foo={}}");
        }

        @Test
        @DisplayName("IllegalArgumentException if value is not a Map")
        public void test1646681583817() {
            assertThrow(() -> marshaller().convertMapToRawData(new Object()))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("Received unsupported type for conversion: \n" +
                                     "Expected heirs of interface java.util.Map\n" +
                                     "Actual: class java.lang.Object\n");
        }

        @Test
        @DisplayName("IllegalArgumentException if map keys is not String")
        public void test1646681617352() {
            final Map<Object, Object> map = new HashMap<>();
            map.put(1, 2);
            assertThrow(() -> marshaller().convertMapToRawData(map))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("Keys in the map must be of type String: Map<String, Object>\n" +
                                     "Unsupported keys: [1]");
        }

    }

    @Nested
    @DisplayName("#convertCollectionToRawData() method tests")
    public class ConvertCollectionToRawDataMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646681100056() {
            assertNPE(() -> marshaller().convertCollectionToRawData(null), "value");
        }

        @Test
        @DisplayName("Convert List<String> to raw data ArrayList")
        public void test1646681122498() {
            final List<String> strings = listOf("foo", "bar");
            final Object result = marshaller().convertCollectionToRawData(strings);
            assertThat(result).isInstanceOf(ArrayList.class);
            assertThat(result.toString()).isEqualTo("[foo, bar]");
        }

        @Test
        @DisplayName("Convert Set<String> to raw data ArrayList")
        public void test1646681143865() {
            final Set<String> strings = setOf("foo", "bar");
            final Object result = marshaller().convertCollectionToRawData(strings);
            assertThat(result).isInstanceOf(ArrayList.class);
            assertThat(result.toString()).isEqualTo("[bar, foo]");
        }

        @Test
        @DisplayName("Convert List<POJO> to raw data ArrayList")
        public void test1646681152122() {
            final List<POJO> strings = listOf(pojo());
            final Object result = marshaller().convertCollectionToRawData(strings);
            assertThat(result).isInstanceOf(ArrayList.class);
            assertThat(result.toString()).isEqualTo("[{}]");
        }

        @Test
        @DisplayName("IllegalArgumentException if value is not Collection")
        public void test1646681219600() {
            assertThrow(() -> marshaller().convertCollectionToRawData(new Object()))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("Received unsupported type for conversion: \n" +
                                     "Expected heirs of interface java.util.Collection\n" +
                                     "Actual: class java.lang.Object\n");
        }
    }

    @Nested
    @DisplayName("#convertArrayToRawData() method tests")
    public class ConvertArrayToRawDataMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646680511311() {
            assertNPE(() -> marshaller().convertArrayToRawData(null), "value");
        }

        @Test
        @DisplayName("Convert String[] to raw data ArrayList")
        public void test1646680541145() {
            final String[] strings = arrayOf("foo", "bar");
            final Object result = marshaller().convertArrayToRawData(strings);
            assertThat(result).isInstanceOf(ArrayList.class);
            assertThat(result.toString()).isEqualTo("[foo, bar]");
        }

        @Test
        @DisplayName("Convert POJO[] to raw data ArrayList")
        public void test1646680692230() {
            final POJO[] strings = arrayOf(pojo());
            final Object result = marshaller().convertArrayToRawData(strings);
            assertThat(result).isInstanceOf(ArrayList.class);
            assertThat(result.toString()).isEqualTo("[{}]");
        }

        @Test
        @DisplayName("IllegalArgumentException if value is not array")
        public void test1646680982447() {
            assertThrow(() -> marshaller().convertArrayToRawData(new Object()))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("Received unsupported type for conversion: \n" +
                                     "Expected: array\n" +
                                     "Actual: java.lang.Object\n");
        }

    }

    @Nested
    @DisplayName("#convertSimpleToRawData() method tests")
    public class ConvertSimpleToRawDataMethodTests {

        @Test
        @DisplayName("Convert null value to empty string")
        public void test1646678153106() {
            assertThat(marshaller().convertSimpleToRawData(null)).isEqualTo("");
        }

        @Test
        @DisplayName("Convert empty string value to empty string")
        public void test1646678216894() {
            assertThat(marshaller().convertSimpleToRawData("")).isEqualTo("");
        }

        @Test
        @DisplayName("Convert Integer value to string")
        public void test1646678233405() {
            assertThat(marshaller().convertSimpleToRawData(1)).isEqualTo("1");
        }

        @Test
        @DisplayName("Convert decoded string to encoded string")
        public void test1646678254265() {
            assertThat(marshaller().convertSimpleToRawData(DECODED)).isEqualTo(ENCODED);
        }

    }

    @Nested
    @DisplayName("#encode() method tests")
    public class EncodeMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646678004644() {
            assertNPE(() -> marshaller().encode(null), "value");
        }

        @Test
        @DisplayName("Encode decoded string")
        public void test1646678016784() {
            assertThat(marshaller().encode(DECODED)).isEqualTo(ENCODED);
        }

    }

    @Nested
    @DisplayName("#decode() method tests")
    public class DecodeMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646677824328() {
            assertNPE(() -> marshaller().decode(null), "value");
        }

        @Test
        @DisplayName("Decode encoded string")
        public void test1646677890242() {
            assertThat(marshaller().decode(ENCODED)).isEqualTo(DECODED);
        }

    }

    private static FormUrlMarshaller marshaller() {
        return new FormUrlMarshaller();
    }

    private static POJO pojo() {
        return new POJO();
    }

    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    @FormUrlEncoded
    public static class POJO extends HashMap<String, Object> {

        public static final POJO CONSTANT = new POJO();

        @FormUrlEncodedField("constant")
        public static final POJO ANNOTATED_CONSTANT = new POJO();

        @FormUrlEncodedField("pojo")
        private POJO pojo;

        @FormUrlEncodedField("missed")
        private String missed;

        @FormUrlEncodedField("stringField")
        private String stringField;

        @FormUrlEncodedField("integerField")
        private Integer integerField;

        @FormUrlEncodedField("objectField")
        private Object objectField;

        @FormUrlEncodedField("stringArrayField")
        private String[] stringArrayField;

        @FormUrlEncodedField("integerArrayField")
        private Integer[] integerArrayField;

        @FormUrlEncodedField("listStringField")
        private List<String> listStringField;

        @FormUrlEncodedField("listIntegerField")
        private List<Integer> listIntegerField;

        @FormUrlEncodedField("mapField")
        private Map<String, Object> mapField;

        @FormUrlEncodedAdditionalProperties()
        private Map<String, Object> additionalProperties;

        @Override
        public String toString() {
            return "toString";
        }

    }
}
