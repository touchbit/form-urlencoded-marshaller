package org.touchbit.www.form.urlencoded.marshaller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.BaseTest;
import org.touchbit.www.form.urlencoded.marshaller.model.MapPojo;
import org.touchbit.www.form.urlencoded.marshaller.model.Pojo;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.util.MarshallerException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
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
            final MapPojo mapPojo = mapPojo().nestedMapPojo(mapPojo().stringField("string_value"));
            mapPojo.put("foo", mapOf("bar", 1));
            final FormUrlMarshaller marshaller = marshaller();
            marshaller.getConfiguration().withPrettyPrint(false);
            assertThat(marshaller.marshal(mapPojo)).isEqualTo("nestedMapPojo[stringField]=string_value&foo[bar]=1");
        }

        @Test
        @DisplayName("Convert pojo map to form url encoded pretty string")
        public void test1646683633833() {
            final MapPojo mapPojo = mapPojo().nestedMapPojo(mapPojo().stringField("string_value"));
            mapPojo.put("foo", mapOf("bar", 1));
            final FormUrlMarshaller marshaller = marshaller();
            marshaller.getConfiguration().withPrettyPrint(true);
            assertThat(marshaller.marshal(mapPojo)).isEqualTo("nestedMapPojo[stringField]=string_value&\nfoo[bar]=1");
        }

        @Test
        @DisplayName("Convert pojo to form url encoded string with implicit list")
        public void test1646683988015() {
            final MapPojo mapPojo = mapPojo().nestedMapPojo(mapPojo().stringField("string_value"));
            mapPojo.put("foo", mapOf("bar", arrayOf(1)));
            final FormUrlMarshaller marshaller = marshaller();
            marshaller.getConfiguration().enableHiddenList().withPrettyPrint(true);
            assertThat(marshaller.marshal(mapPojo)).isEqualTo("nestedMapPojo[stringField]=string_value&\nfoo[bar]=1");
        }

        @Test
        @DisplayName("Convert pojo to form url encoded string with implicit list")
        public void test1646683893206() {
            final MapPojo mapPojo = mapPojo().nestedMapPojo(mapPojo().stringField("string_value"));
            mapPojo.put("foo", mapOf("bar", arrayOf(1)));
            final FormUrlMarshaller marshaller = marshaller();
            marshaller.getConfiguration().enableImplicitList().withPrettyPrint(false);
            assertThat(marshaller.marshal(mapPojo)).isEqualTo("nestedMapPojo[stringField]=string_value&foo[bar][]=1");
        }

        @Test
        @DisplayName("Convert pojo to form url encoded string with explicit list")
        public void test1646683947701() {
            final MapPojo mapPojo = mapPojo().nestedMapPojo(mapPojo().stringField("string_value"));
            mapPojo.put("foo", mapOf("bar", arrayOf(1)));
            final FormUrlMarshaller marshaller = marshaller();
            marshaller.getConfiguration().enableExplicitList().withPrettyPrint(false);
            assertThat(marshaller.marshal(mapPojo)).isEqualTo("nestedMapPojo[stringField]=string_value&foo[bar][0]=1");
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
            final MapPojo mapPojo = mapPojo().stringField("foo");
            mapPojo.put("bar", mapPojo().mapObjectField(mapOf("map_key", null)));
            final Object result = marshaller().convertValueToRawData(mapPojo);
            assertThat(result.toString()).isEqualTo("{stringField=foo, bar={mapObjectField={map_key=}}}");
        }

        @Test
        @DisplayName("Convert List<POJO> value to raw data ArrayList")
        public void test1646682943678() {
            final Object result = marshaller().convertValueToRawData(listOf(null, mapPojo()));
            assertThat(result.toString()).isEqualTo("[, {}]");
        }

        @Test
        @DisplayName("Convert POJO[] value to raw data ArrayList")
        public void test1646682985063() {
            final Object result = marshaller().convertValueToRawData(arrayOf(null, mapPojo()));
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
            final MapPojo mapPojo = mapPojo().stringField(DECODED);
            final Map<String, Object> map = marshaller().convertPojoToRawData(mapPojo);
            assertThat(map.toString()).isEqualTo("{stringField=" + ENCODED + "}");
        }

        @Test
        @DisplayName("Convert POJO to raw data Map with array field")
        public void test1646682111744() {
            final MapPojo mapPojo = mapPojo().arrayStringField(arrayOf("foo"));
            final Map<String, Object> map = marshaller().convertPojoToRawData(mapPojo);
            assertThat(map.toString()).isEqualTo("{arrayStringField=[foo]}");
        }

        @Test
        @DisplayName("Convert POJO to raw data Map with nested POJO field")
        public void test1646682159512() {
            final MapPojo mapPojo = mapPojo().nestedMapPojo(mapPojo().stringField("foo"));
            final Map<String, Object> map = marshaller().convertPojoToRawData(mapPojo);
            assertThat(map.toString()).isEqualTo("{nestedMapPojo={stringField=foo}}");
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
            final Map<String, Object> map = mapOf("foo", mapPojo());
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
            final List<MapPojo> strings = listOf(mapPojo());
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
            final MapPojo[] strings = arrayOf(mapPojo());
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
        @DisplayName("Decode encoded string")
        public void test1646677890242() {
            assertThat(marshaller().decode(ENCODED)).isEqualTo(DECODED);
        }

        @Test
        @DisplayName("Decode null string")
        public void test1646786100739() {
            assertThat(marshaller().decode(null)).isEqualTo(null);
        }

    }

    @Nested
    @DisplayName("#writeRawDataToPojo() method tests")
    public class WriteRawDataToPojoMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646750228257() {
            assertNPE(() -> marshaller().writeRawDataToPojo(null, mapOf()), "model");
            assertNPE(() -> marshaller().writeRawDataToPojo(mapPojo(), null), "rawData");
        }

        @Test
        @DisplayName("Write to POJO without AdditionalProperty")
        public void test1646750291689() {
            final Map<String, Object> rawData = mapPojo().stringField("foo").toMap();
            final MapPojo model = mapPojo();
            marshaller().writeRawDataToPojo(model, rawData);
            assertThat(model.stringField()).isEqualTo("foo");
            assertThat(model.additionalProperties()).isNull();
        }

        @Test
        @DisplayName("Write to POJO with AdditionalProperty")
        public void test1646750770814() {
            final Map<String, Object> rawData = mapPojo().stringField("foo").toMap();
            rawData.put("bar", "test");
            final MapPojo model = mapPojo();
            marshaller().writeRawDataToPojo(model, rawData);
            assertThat(model.stringField()).isEqualTo("foo");
            assertThat(model.additionalProperties()).isNotNull();
            assertThat(model.additionalProperties().toString()).isEqualTo("{bar=test}");
        }

        @Test
        @DisplayName("Write to POJO with nested POJO ({nestedPojo={integerField=123}:Pojo.class}:Pojo.class)")
        public void test1646912537691() {
            final String integerFieldName = Pojo.PojoFields.INTEGER_FIELD.getFieldName();
            final String nestedPojoFieldName = Pojo.PojoFields.NESTED_POJO.getFieldName();
            final Map<String, Object> rawData = mapOf(nestedPojoFieldName, mapOf(integerFieldName, "123"));
            final Pojo pojo = pojo();
            marshaller().writeRawDataToPojo(pojo, rawData);
            assertThat(pojo.nestedPojo()).isNotNull();
            assertThat(pojo.nestedPojo().integerField()).isEqualTo(123);
        }

        @Test
        @DisplayName("Write Map<String, Object> to List<POJO>")
        public void test1646915835416() {
            final String integerFieldName = Pojo.PojoFields.INTEGER_FIELD.getFieldName();
            final String listPojoFieldName = Pojo.PojoFields.LIST_POJO_FIELD.getFieldName();
            final Map<String, Object> rawData = mapOf(listPojoFieldName, mapOf(integerFieldName, "123"));
            final Pojo pojo = pojo();
            marshaller().writeRawDataToPojo(pojo, rawData);
            assertThat(pojo.listPojoField()).isNotEmpty();
            assertThat(pojo.listPojoField().get(0).integerField()).isEqualTo(123);
        }

        @Test
        @DisplayName("Write Map<String, Object> to POJO[]")
        public void test1646922136573() {
            final String integerFieldName = Pojo.PojoFields.INTEGER_FIELD.getFieldName();
            final String arrayPojoFieldName = Pojo.PojoFields.ARRAY_POJO_FIELD.getFieldName();
            final Map<String, Object> rawData = mapOf(arrayPojoFieldName, mapOf(integerFieldName, "123"));
            final Pojo pojo = pojo();
            marshaller().writeRawDataToPojo(pojo, rawData);
            assertThat(pojo.arrayPojoField()).isNotEmpty();
            assertThat(pojo.arrayPojoField()[0].integerField()).isEqualTo(123);
        }

        @Test
        @DisplayName("MarshallerException: Incompatible types received for conversion.")
        public void test1646924870841() {
            final String arrayPojoFieldName = Pojo.PojoFields.NESTED_POJO.getFieldName();
            final Map<String, Object> rawData = mapOf(arrayPojoFieldName, listOf("123"));
            final Pojo pojo = pojo();
            assertThrow(() -> marshaller().writeRawDataToPojo(pojo, rawData))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("Incompatible types received for conversion.\n" +
                                     "    Source: {nestedPojo=[123]}\n" +
                                     "    Source field: nestedPojo\n" +
                                     "    Source value: [123]\n" +
                                     "    Source type: java.util.ArrayList\n" +
                                     "    Target type: " + Pojo.class.getTypeName() + "\n" +
                                     "    Target field: private Pojo nestedPojo;\n");
        }

        @Test
        @DisplayName("MarshallerException: if raw data != Map (Incompatible types)")
        public void test1646913108816() {
            final String nestedPojoFieldName = Pojo.PojoFields.NESTED_POJO.getFieldName();
            final Map<String, Object> rawData = mapOf(nestedPojoFieldName, UTF_8);
            final Pojo pojo = pojo();
            assertThrow(() -> marshaller().writeRawDataToPojo(pojo, rawData))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("Incompatible types received for conversion.\n" +
                                     "    Source: {nestedPojo=UTF-8}\n" +
                                     "    Source field: nestedPojo\n" +
                                     "    Source value: UTF-8\n" +
                                     "    Source type: sun.nio.cs.UTF_8\n" +
                                     "    Target type: " + Pojo.class.getTypeName() + "\n" +
                                     "    Target field: private Pojo nestedPojo;\n");
        }

    }

    @SuppressWarnings("unchecked")
    @Nested
    @DisplayName("#convertRawValueToTargetJavaType() method tests")
    public class ConvertRawValueToTargetJavaTypeMethodTests {

        @Test
        @DisplayName("Convert List<List<String>> to field generic type List<List<Integer>>")
        public void test1646766412735() {
            final Type genericType = MapPojo.PojoFields.LIST_LIST_INTEGER_FIELD.getGenericType();
            final List<List<String>> rawData = listOf(listOf("1"), listOf("2"));
            final Object result = marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result.toString()).isEqualTo("[[1], [2]]");
        }

        @Test
        @DisplayName("Convert List<List<String>> to field generic type List<Integer[]>")
        public void test1646766614821() {
            final Type genericType = MapPojo.PojoFields.LIST_ARRAY_INTEGER_FIELD.getGenericType();
            final List<List<Object>> rawData = listOf(listOf("1", null, "2"), listOf("3"));
            final Object result = marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result).isInstanceOf(List.class);
            final List<Integer[]> resultList = (List<Integer[]>) result;
            assertThat(resultList.get(0)).containsExactly(1, null, 2);
            assertThat(resultList.get(1)).containsExactly(3);
        }

        @Test
        @DisplayName("Convert List<String> to field generic type List<Object>")
        public void test1646783643724() {
            final Type genericType = MapPojo.PojoFields.LIST_OBJECT_FIELD.getGenericType();
            final List<String> rawData = listOf("1", "2");
            final List<Object> result = (List<Object>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result).containsExactly("1", "2");
        }

        @Test
        @DisplayName("Convert List<String> to field generic type List (raw generic list)")
        public void test1646785738544() {
            final Type genericType = MapPojo.PojoFields.LIST_RAW_FIELD.getGenericType();
            final List<String> rawData = listOf("1", "2");
            final List<Object> result = (List<Object>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result).containsExactly("1", "2");
        }

        @Test
        @DisplayName("Convert List<Map<?, ?>> to field generic type List<Map<?, ?>>")
        public void test1646786782505() {
            final Type genericType = MapPojo.PojoFields.LIST_MAP_STRING_INTEGER_FIELD.getGenericType();
            final List<Map<String, Object>> rawData = listOf(mapOf("foo", 1));
            final List<Map<String, Object>> result =
                    (List<Map<String, Object>>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result.get(0).get("foo")).isEqualTo(1);
        }

        @Test
        @DisplayName("Convert List<Map<?, ?>> to List (raw type)")
        public void test1646830967212() {
            final Type genericType = MapPojo.PojoFields.LIST_RAW_FIELD.getGenericType();
            final List<Map<String, Object>> rawData = listOf(mapOf("foo", "bar"));
            final List<Map<String, Object>> result =
                    (List<Map<String, Object>>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result.get(0).get("foo")).isEqualTo("bar");
        }

        @Test
        @DisplayName("Convert List<Map<?, ?>> to field generic type Map<?, ?>[]")
        public void test1646786885325() {
            final Type genericType = MapPojo.PojoFields.ARRAY_MAP_OBJECT_FIELD.getGenericType();
            final List<Map<String, Object>> rawData = listOf(mapOf("foo", "bar"));
            final Map<String, Object>[] result =
                    (Map<String, Object>[]) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result[0].get("foo")).isEqualTo("bar");
        }

        @Test
        @DisplayName("Convert String to field generic type Integer[] (hidden url encoded array)")
        public void test1646770464530() {
            final Type genericType = MapPojo.PojoFields.ARRAY_INTEGER_FIELD.getGenericType();
            final String rawData = "1";
            final Object result = marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            final Integer[] resultArray = (Integer[]) result;
            assertThat(resultArray).containsExactly(1);
        }

        @Test
        @DisplayName("Convert String to field generic type List<Integer> (hidden url encoded array)")
        public void test1646782895456() {
            final Type genericType = MapPojo.PojoFields.LIST_INTEGER_FIELD.getGenericType();
            final String rawData = "1";
            final List<Integer> result = (List<Integer>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result.get(0)).isEqualTo(1);
        }

        @Test
        @DisplayName("Convert Map<String, ?> to field generic type List<Map<String, ?>> (hidden url encoded array)")
        public void test1646782908308() {
            final Type genericType = MapPojo.PojoFields.LIST_MAP_STRING_INTEGER_FIELD.getGenericType();
            final Map<String, Object> rawData = mapOf("foo", 1);
            final List<Map<String, Integer>> result =
                    (List<Map<String, Integer>>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result.get(0).get("foo")).isEqualTo(1);
        }

        @Test
        @DisplayName("Convert Map<String, ?> to field generic type Map<String, ?>[] (hidden url encoded array)")
        public void test1646783865319() {
            final Type genericType = MapPojo.PojoFields.ARRAY_MAP_OBJECT_FIELD.getGenericType();
            final Map<String, Object> rawData = mapOf("foo", "1");
            final Map<String, Object>[] result =
                    (Map<String, Object>[]) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result[0].get("foo")).isEqualTo("1");
        }

        @Test
        @DisplayName("Convert Map<String, ?> to field generic type Map[] (hidden url encoded array)")
        public void test1646828259346() {
            final Type genericType = MapPojo.PojoFields.ARRAY_RAW_MAP_FIELD.getGenericType();
            final Map<String, Object> rawData = mapOf("foo", "1");
            final Map<String, Object>[] result =
                    (Map<String, Object>[]) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result[0].get("foo")).isEqualTo("1");
        }

        @Test
        @DisplayName("Convert Map<String, Map<String, String>> to field generic type Map<String, Map<String, Integer>>")
        public void test1646771360667() {
            final Type genericType = MapPojo.PojoFields.MAP_MAP_INTEGER.getGenericType();
            final Map<String, Object> rawData = mapOf("foo", mapOf("bar", 1));
            final Map<String, Map<String, Object>> result =
                    (Map<String, Map<String, Object>>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result.get("foo").get("bar")).isEqualTo(1);
        }

        @Test
        @DisplayName("Convert Map<String, Map<String, Object>> to field generic type Map<String, Object>")
        public void test1646772270818() {
            final Type genericType = MapPojo.PojoFields.MAP_OBJECT_FIELD.getGenericType();
            final Map<String, Object> rawData = mapOf("foo", mapOf("bar", true, "var", "str", "car", 222));
            final Map<String, Map<String, Object>> result =
                    (Map<String, Map<String, Object>>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result.get("foo").get("bar")).isEqualTo(true);
            assertThat(result.get("foo").get("var")).isEqualTo("str");
            assertThat(result.get("foo").get("car")).isEqualTo(222);
        }

        @Test
        @DisplayName("Convert Map<String, Object> to field generic type POJO (extended from Map<String, Object>)")
        public void test1646870688987() {
            final String fieldName = MapPojo.PojoFields.INTEGER_FIELD.getFieldName();
            final Type genericType = MapPojo.PojoFields.NESTED_MAP_POJO.getGenericType();
            final Map<String, Object> rawData = mapOf(fieldName, "123", "foo", "123");
            final Map<String, Object> pojo =
                    (Map<String, Object>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(pojo.get("foo")).isEqualTo("123");
            assertThat(pojo.get(fieldName)).isEqualTo("123");
        }

        @Test
        @DisplayName("MarshallerException: Incompatible types: Map<String, Object> to POJO")
        public void test1646908301203() {
            final String integerFieldName = Pojo.PojoFields.INTEGER_FIELD.getFieldName();
            final String nestedPojoFieldName = Pojo.PojoFields.NESTED_POJO.getFieldName();
            final Type genericType = Pojo.PojoFields.NESTED_POJO.getGenericType();
            final Map<String, Object> rawData = mapOf(nestedPojoFieldName, mapOf(integerFieldName, "123"));
            assertThrow(() -> marshaller().convertRawValueToTargetJavaType(rawData, genericType))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("Incompatible types received for conversion.\n" +
                                     "    Source value: {nestedPojo={integerField=123}}\n" +
                                     "    Source type: java.util.HashMap\n" +
                                     "    Target type: " + Pojo.class.getTypeName() + "\n");
        }

    }

    @Test
    @DisplayName("MapPojo")
    public void test1646872534663() {
        Map<String, Object> aaaa = new HashMap<>();
        Class<? extends Map> asdasdasd = aaaa.getClass();
        System.out.println(" 1 >>>>>> " + asdasdasd.getGenericSuperclass());
        System.out.println(" 1 >>>>>> " + Arrays.toString(asdasdasd.getTypeParameters()));
        final ParameterizedType genericSuperclass111 = (ParameterizedType) asdasdasd.getGenericSuperclass();
        final Type[] actualTypeArguments = genericSuperclass111.getActualTypeArguments();
        for (Type actualTypeArgument : actualTypeArguments) {
            TypeVariable typeVariable = (TypeVariable) actualTypeArgument;
            System.out.println(" 20 >>>>>> " + Arrays.toString(typeVariable.getBounds()));
            System.out.println(" 21 >>>>>> " + Arrays.toString(typeVariable.getAnnotatedBounds()));
            System.out.println(" 22 >>>>>> " + typeVariable.getGenericDeclaration());
        }
        final Class<MapPojo> mapPojoClass = MapPojo.class;
        System.out.println(" >>>>>> 3 " + mapPojoClass.getGenericSuperclass());
        final ParameterizedType genericSuperclass = (ParameterizedType) mapPojoClass.getGenericSuperclass();
        System.out.println(" >>>>>> 4 " + Arrays.toString(genericSuperclass.getActualTypeArguments()));
        for (Type actualTypeArgument : genericSuperclass.getActualTypeArguments()) {
            System.out.println(" >>>>>> 5 " + actualTypeArgument.getClass());
            System.out.println(" >>>>>> 6 " + actualTypeArgument.getTypeName());
        }
    }


    private static FormUrlMarshaller marshaller() {
        return new FormUrlMarshaller();
    }

    private static MapPojo mapPojo() {
        return new MapPojo();
    }

    private static Pojo pojo() {
        return new Pojo();
    }


}
