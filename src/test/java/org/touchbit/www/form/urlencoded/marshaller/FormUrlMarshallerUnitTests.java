package org.touchbit.www.form.urlencoded.marshaller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.www.form.urlencoded.marshaller.util.MarshallerException;
import qa.model.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.touchbit.www.form.urlencoded.marshaller.NullValueRule.*;

@SuppressWarnings("unchecked")
@DisplayName("FormUrlMarshaller.class unit tests")
public class FormUrlMarshallerUnitTests extends BaseTest {

    @Nested
    @DisplayName("Constructor tests")
    public class ConstructorTests {

        @Test
        @DisplayName("Class default parameters")
        public void test1647101169913() {
            final FormUrlMarshaller marshaller = new FormUrlMarshaller();
            assertTrue(marshaller.isHiddenList());
            assertFalse(marshaller.isExplicitList());
            assertFalse(marshaller.isImplicitList());
            assertFalse(marshaller.isProhibitAdditionalProperties());
            assertIs(marshaller.getFormUrlCodingCharset(), UTF_8);
        }

        @Test
        @DisplayName("Url encoded list type")
        public void test1647101299749() {
            final FormUrlMarshaller marshaller = new FormUrlMarshaller();
            assertTrue(marshaller.isHiddenList());
            assertFalse(marshaller.isExplicitList());
            assertFalse(marshaller.isImplicitList());
            marshaller.enableExplicitList();
            assertFalse(marshaller.isHiddenList());
            assertTrue(marshaller.isExplicitList());
            assertFalse(marshaller.isImplicitList());
            marshaller.enableImplicitList();
            assertFalse(marshaller.isHiddenList());
            assertFalse(marshaller.isExplicitList());
            assertTrue(marshaller.isImplicitList());
            marshaller.enableHiddenList();
            assertTrue(marshaller.isHiddenList());
            assertFalse(marshaller.isExplicitList());
            assertFalse(marshaller.isImplicitList());
        }

        @Test
        @DisplayName("CodingCharset")
        public void test1647101412967() {
            final FormUrlMarshaller marshaller = new FormUrlMarshaller();
            assertIs(marshaller.getFormUrlCodingCharset(), UTF_8);
            assertIs(marshaller.setFormUrlCodingCharset(UTF_16).getFormUrlCodingCharset(), UTF_16);
        }

        @Test
        @DisplayName("ProhibitAdditionalProperties")
        public void test1647101478320() {
            final FormUrlMarshaller marshaller = new FormUrlMarshaller();
            assertFalse(marshaller.isProhibitAdditionalProperties());
            marshaller.prohibitAdditionalProperties(true);
            assertTrue(marshaller.isProhibitAdditionalProperties());
        }

        @Test
        @DisplayName("NullValueRule")
        public void test1647111089607() {
            final FormUrlMarshaller marshaller = new FormUrlMarshaller();
            assertIs(marshaller.getNullValueRule(), RULE_IGNORE);
            final LittlePojo pojo = new LittlePojo();
            assertThat(marshaller.marshal(pojo)).isEmpty();
            assertThat(marshaller.setNullValueRule(RULE_NULL_STRING).marshal(pojo)).isEqualTo("bar=null&foo=null");
            assertThat(marshaller.setNullValueRule(RULE_EMPTY_STRING).marshal(pojo)).isEqualTo("bar=&foo=");
            assertThat(marshaller.setNullValueRule(RULE_NULL_MARKER).marshal(pojo)).isEqualTo("bar=%00&foo=%00");
        }

    }

    @Nested
    @DisplayName("#marshal() method tests")
    public class MarshalMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646683219120() {
            assertRequired(() -> marshaller().marshal(null), "model");
        }

        @Test
        @DisplayName("Convert pojo map to form url encoded string")
        public void test1646683248748() {
            final MapPojo mapPojo = mapPojo().nestedMapPojo(mapPojo().string("string_value"));
            mapPojo.put("foo", mapOf("bar", 1));
            final FormUrlMarshaller marshaller = marshaller();
            assertThat(marshaller.marshal(mapPojo)).isEqualTo("foo[bar]=1&nestedMapPojo[string]=string_value");
        }

        @Test
        @DisplayName("Convert pojo to form url encoded string with implicit list")
        public void test1646683988015() {
            final MapPojo mapPojo = mapPojo().nestedMapPojo(mapPojo().string("string_value"));
            mapPojo.put("foo", mapOf("bar", arrayOf(1, 2)));
            final FormUrlMarshaller marshaller = marshaller().enableHiddenList();
            assertThat(marshaller.marshal(mapPojo))
                    .isEqualTo("foo[bar]=1&foo[bar]=2&nestedMapPojo[string]=string_value");
        }

        @Test
        @DisplayName("Convert pojo to form url encoded string with implicit list")
        public void test1646683893206() {
            final MapPojo mapPojo = mapPojo().nestedMapPojo(mapPojo().string("string_value"));
            mapPojo.put("foo", mapOf("bar", arrayOf(1, 2)));
            final FormUrlMarshaller marshaller = marshaller().enableImplicitList();
            assertThat(marshaller.marshal(mapPojo))
                    .isEqualTo("foo[bar][]=1&foo[bar][]=2&nestedMapPojo[string]=string_value");
        }

        @Test
        @DisplayName("Convert pojo to form url encoded string with explicit list")
        public void test1646683947701() {
            final Pojo mapPojo = pojo().nestedPojo(pojo().arrayString(arrayOf("s1", "s2")));
            final FormUrlMarshaller marshaller = marshaller().enableExplicitList();
            assertThat(marshaller.marshal(mapPojo))
                    .isEqualTo("nestedPojo[arrayString][0]=s1&nestedPojo[arrayString][1]=s2");
        }

        @Test
        @DisplayName("Convert pojo with additionalProperties")
        public void test1647116805713() {
            final Pojo pojo = pojo().additionalProperties(mapOf("foo", "car", "bar", "lar"));
            assertThat(marshaller().marshal(pojo)).isEqualTo("bar=lar&foo=car");
        }

        @Test
        @DisplayName("Convert pojo if additionalProperties contains nested pojo with additionalProperties")
        public void test1647117098739() {
            final Pojo pojo = pojo().additionalProperties(mapOf("nested", pojo().additionalProperties(mapOf("foo", "bar"))));
            assertThat(marshaller().marshal(pojo)).isEqualTo("nested[foo]=bar");
        }

        @Test
        @DisplayName("Overwrite pojo field if additionalProperties contains same key")
        public void test1647117208258() {
            final Pojo pojo = pojo().string("foo").additionalProperties(mapOf("string", "bar"));
            assertThat(marshaller().marshal(pojo)).isEqualTo("string=bar");
        }

        @Test
        @DisplayName("Convert Map to form url encoded string with explicit list")
        public void test1647109833581() {
            final Map<String, Object> map = mapOf("foo", mapOf("bar", arrayOf(1, 2)));
            assertThat(marshaller().enableExplicitList().marshal(map))
                    .isEqualTo("foo[bar][0]=1&foo[bar][1]=2");
        }

        @Test
        @DisplayName("MarshallerException if model type is not supported")
        public void test1646684025352() {
            assertThrow(() -> marshaller().marshal(new Object()))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Received unsupported type for conversion.\n" +
                                     "    Actual type: java.lang.Object\n" +
                                     "    Expected: heirs of java.util.Map\n" +
                                     "    Expected: POJO classes with @FormUrlEncoded annotation\n");
        }

        @Test
        @DisplayName("MarshallerException: Unexpected marshalling error")
        public void test1647101811830() {
            assertThrow(() -> marshaller().marshal(new BrokenMap<>()))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Unexpected marshalling error.\n" +
                                     "    Error cause:\n" +
                                     "     - UnsupportedOperationException: BrokenMap for test\n");
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
            final MapPojo mapPojo = mapPojo().string("foo");
            mapPojo.put("bar", mapPojo().mapObject(mapOf("map_key", null)));
            final Object result = marshaller().convertValueToRawData(mapPojo);
            assertThat(result.toString()).isEqualTo("{string=foo, bar={mapObject={map_key=}}}");
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
        @DisplayName("MarshallerException if value type is not supported")
        public void test1646683148875() {
            assertThrow(() -> marshaller().convertValueToRawData(new Object()))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Received unsupported type for conversion.\n" +
                                     "    Actual type: java.lang.Object\n" +
                                     "    Expected: simple reference types (String, Integer, Boolean, etc.)\n" +
                                     "    Expected: POJO classes with @FormUrlEncoded annotation\n" +
                                     "    Expected: heirs of java.util.Map\n" +
                                     "    Expected: heirs of java.util.Collection\n" +
                                     "    Expected: simple/complex reference type array (String[], POJO[], etc.)\n");
        }
    }

    @Nested
    @DisplayName("#convertPojoToRawData() method tests")
    public class ConvertPojoToRawDataMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646681756463() {
            assertRequired(() -> marshaller().convertPojoToRawData(null), "value");
        }

        @Test
        @DisplayName("Convert POJO to raw data Map with simple field")
        public void test1646681989639() {
            final MapPojo mapPojo = mapPojo().string(DECODED);
            final Map<String, Object> map = marshaller().convertPojoToRawData(mapPojo);
            assertThat(map.toString()).isEqualTo("{string=" + ENCODED + "}");
        }

        @Test
        @DisplayName("Convert POJO to raw data Map with encoded simple field")
        public void test1647098635333() {
            final MapPojo mapPojo = mapPojo().encodedString(ENCODED);
            final Map<String, Object> map = marshaller().convertPojoToRawData(mapPojo);
            assertThat(map.toString()).isEqualTo("{encodedString=" + ENCODED + "}");
        }

        @Test
        @DisplayName("Convert POJO to raw data Map with array field")
        public void test1646682111744() {
            final MapPojo mapPojo = mapPojo().arrayString(arrayOf("foo", "bar"));
            final Map<String, Object> map = marshaller().convertPojoToRawData(mapPojo);
            assertThat(map.toString()).isEqualTo("{arrayString=[foo, bar]}");
        }

        @Test
        @DisplayName("Convert POJO to raw data Map with nested POJO field")
        public void test1646682159512() {
            final MapPojo mapPojo = mapPojo().nestedMapPojo(mapPojo().string("foo"));
            final Map<String, Object> map = marshaller().convertPojoToRawData(mapPojo);
            assertThat(map.toString()).isEqualTo("{nestedMapPojo={string=foo}}");
        }

        @Test
        @DisplayName("MarshallerException class does not contain a FormUrlEncodedField annotation")
        public void test1646682319880() {
            assertThrow(() -> marshaller().convertPojoToRawData(new Object()))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Class does not contain a required annotation.\n" +
                                     "    Source: class java.lang.Object\n" +
                                     "    Expected: @FormUrlEncoded\n");
        }

    }

    @Nested
    @DisplayName("#convertMapToRawData() method tests")
    public class ConvertMapToRawDataMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646681315876() {
            assertRequired(() -> marshaller().convertMapToRawData(null), "value");
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
        @DisplayName("MarshallerException if value is not a Map")
        public void test1646681583817() {
            assertThrow(() -> marshaller().convertMapToRawData(new Object()))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Received unsupported type for conversion.\n" +
                                     "    Actual type: java.lang.Object\n" +
                                     "    Expected: heirs of java.util.Map\n");
        }

        @Test
        @DisplayName("MarshallerException if map keys is not String")
        public void test1646681617352() {
            final Map<Object, Object> map = new HashMap<>();
            map.put(100500, 2);
            map.put(true, 2);
            assertThrow(() -> marshaller().convertMapToRawData(map))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Invalid Map keys type\n" +
                                     "    Actual: key-type pairs: {100500=Integer, true=Boolean}\n" +
                                     "    Expected: String key type (Map<String, ?>)\n");
        }

    }

    @Nested
    @DisplayName("#convertCollectionToRawData() method tests")
    public class ConvertCollectionToRawDataMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646681100056() {
            assertRequired(() -> marshaller().convertCollectionToRawData(null), "value");
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
        @DisplayName("MarshallerException if value is not Collection")
        public void test1646681219600() {
            assertThrow(() -> marshaller().convertCollectionToRawData(new Object()))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Received unsupported type for conversion.\n" +
                                     "    Actual type: java.lang.Object\n" +
                                     "    Expected: heirs of java.util.Collection\n");
        }
    }

    @Nested
    @DisplayName("#convertArrayToRawData() method tests")
    public class ConvertArrayToRawDataMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646680511311() {
            assertRequired(() -> marshaller().convertArrayToRawData(null), "value");
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
        @DisplayName("MarshallerException if value is not array")
        public void test1646680982447() {
            assertThrow(() -> marshaller().convertArrayToRawData(new Object()))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Received unsupported type for conversion.\n" +
                                     "    Actual type: java.lang.Object\n" +
                                     "    Expected: simple/complex reference type array (String[], POJO[], etc.)\n");
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
    @DisplayName("#writeRawDataToPojo() method tests")
    public class WriteRawDataToPojoMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646750228257() {
            assertRequired(() -> marshaller().writeRawDataToPojo(null, mapOf()), "model");
            assertRequired(() -> marshaller().writeRawDataToPojo(mapPojo(), null), "rawData");
        }

        @Test
        @DisplayName("Write to POJO without AdditionalProperty")
        public void test1646750291689() {
            final Map<String, Object> rawData = mapPojo().string("foo").toMap();
            final MapPojo model = mapPojo();
            marshaller().writeRawDataToPojo(model, rawData);
            assertThat(model.string()).isEqualTo("foo");
            assertThat(model.additionalProperties()).isNull();
        }

        @Test
        @DisplayName("Write to POJO with AdditionalProperty")
        public void test1646750770814() {
            final Map<String, Object> rawData = mapPojo().string("foo").toMap();
            rawData.put("bar", "test");
            final MapPojo model = mapPojo();
            marshaller().writeRawDataToPojo(model, rawData);
            assertThat(model.string()).isEqualTo("foo");
            assertThat(model.additionalProperties()).isNotNull();
            assertThat(model.additionalProperties().toString()).isEqualTo("{bar=test}");
        }

        @Test
        @DisplayName("Write to POJO with nested POJO ({nestedPojo={integer=123}:Pojo.class}:Pojo.class)")
        public void test1646912537691() {
            final String integerFieldName = Pojo.PojoFields.INTEGER.getFieldName();
            final String nestedPojoFieldName = Pojo.PojoFields.NESTED_POJO.getFieldName();
            final Map<String, Object> rawData = mapOf(nestedPojoFieldName, mapOf(integerFieldName, "123"));
            final Pojo pojo = pojo();
            marshaller().writeRawDataToPojo(pojo, rawData);
            assertThat(pojo.nestedPojo()).isNotNull();
            assertThat(pojo.nestedPojo().integer()).isEqualTo(123);
        }

        @Test
        @DisplayName("Write Map<String, Object> to List<POJO>")
        public void test1646915835416() {
            final String integerFieldName = Pojo.PojoFields.INTEGER.getFieldName();
            final String listPojoFieldName = Pojo.PojoFields.LIST_POJO.getFieldName();
            final Map<String, Object> rawData = mapOf(listPojoFieldName, mapOf(integerFieldName, "123"));
            final Pojo pojo = pojo();
            marshaller().writeRawDataToPojo(pojo, rawData);
            assertThat(pojo.listPojo()).isNotEmpty();
            assertThat(pojo.listPojo().get(0).integer()).isEqualTo(123);
        }

        @Test
        @DisplayName("Write Map<String, Object> to POJO[]")
        public void test1646922136573() {
            final String integerFieldName = Pojo.PojoFields.INTEGER.getFieldName();
            final String arrayPojoFieldName = Pojo.PojoFields.ARRAY_POJO.getFieldName();
            final Map<String, Object> rawData = mapOf(arrayPojoFieldName, mapOf(integerFieldName, "123"));
            final Pojo pojo = pojo();
            marshaller().writeRawDataToPojo(pojo, rawData);
            assertThat(pojo.arrayPojo()).isNotEmpty();
            assertThat(pojo.arrayPojo()[0].integer()).isEqualTo(123);
        }

        @Test
        @DisplayName("MarshallerException: Incompatible types received for conversion.")
        public void test1646924870841() {
            final String arrayPojoFieldName = Pojo.PojoFields.NESTED_POJO.getFieldName();
            final Map<String, Object> rawData = mapOf(arrayPojoFieldName, listOf("123"));
            final Pojo pojo = pojo();
            assertThrow(() -> marshaller().writeRawDataToPojo(pojo, rawData))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Incompatible types received for conversion.\n" +
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
                    .assertMessageIs("\n  Incompatible types received for conversion.\n" +
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
            final Type genericType = MapPojo.PojoFields.LIST_LIST_INTEGER.getGenericType();
            final List<List<String>> rawData = listOf(listOf("1"), listOf("2"));
            final Object result = marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result.toString()).isEqualTo("[[1], [2]]");
        }

        @Test
        @DisplayName("Convert List<List<String>> to field generic type List<Integer[]>")
        public void test1646766614821() {
            final Type genericType = MapPojo.PojoFields.LIST_ARRAY_INTEGER.getGenericType();
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
            final Type genericType = MapPojo.PojoFields.LIST_OBJECT.getGenericType();
            final List<String> rawData = listOf("1", "2");
            final List<Object> result = (List<Object>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result).containsExactly("1", "2");
        }

        @Test
        @DisplayName("Convert List<String> to field generic type List (raw generic list)")
        public void test1646785738544() {
            final Type genericType = MapPojo.PojoFields.LIST_RAW.getGenericType();
            final List<String> rawData = listOf("1", "2");
            final List<Object> result = (List<Object>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result).containsExactly("1", "2");
        }

        @Test
        @DisplayName("Convert List<Map<?, ?>> to field generic type List<Map<?, ?>>")
        public void test1646786782505() {
            final Type genericType = MapPojo.PojoFields.LIST_MAP_STRING_INTEGER.getGenericType();
            final List<Map<String, Object>> rawData = listOf(mapOf("foo", 1));
            final List<Map<String, Object>> result =
                    (List<Map<String, Object>>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result.get(0).get("foo")).isEqualTo(1);
        }

        @Test
        @DisplayName("Convert List<Map<?, ?>> to List (raw type)")
        public void test1646830967212() {
            final Type genericType = MapPojo.PojoFields.LIST_RAW.getGenericType();
            final List<Map<String, Object>> rawData = listOf(mapOf("foo", "bar"));
            final List<Map<String, Object>> result =
                    (List<Map<String, Object>>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result.get(0).get("foo")).isEqualTo("bar");
        }

        @Test
        @DisplayName("Convert List<Map<?, ?>> to field generic type Map<?, ?>[]")
        public void test1646786885325() {
            final Type genericType = MapPojo.PojoFields.ARRAY_MAP_OBJECT.getGenericType();
            final List<Map<String, Object>> rawData = listOf(mapOf("foo", "bar"));
            final Map<String, Object>[] result =
                    (Map<String, Object>[]) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result[0].get("foo")).isEqualTo("bar");
        }

        @Test
        @DisplayName("Convert String to field generic type Integer[] (hidden url encoded array)")
        public void test1646770464530() {
            final Type genericType = MapPojo.PojoFields.ARRAY_INTEGER.getGenericType();
            final String rawData = "1";
            final Object result = marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            final Integer[] resultArray = (Integer[]) result;
            assertThat(resultArray).containsExactly(1);
        }

        @Test
        @DisplayName("Convert String to field generic type List<Integer> (hidden url encoded array)")
        public void test1646782895456() {
            final Type genericType = MapPojo.PojoFields.LIST_INTEGER.getGenericType();
            final String rawData = "1";
            final List<Integer> result = (List<Integer>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result.get(0)).isEqualTo(1);
        }

        @Test
        @DisplayName("Convert Map<String, ?> to field generic type List<Map<String, ?>> (hidden url encoded array)")
        public void test1646782908308() {
            final Type genericType = MapPojo.PojoFields.LIST_MAP_STRING_INTEGER.getGenericType();
            final Map<String, Object> rawData = mapOf("foo", 1);
            final List<Map<String, Integer>> result =
                    (List<Map<String, Integer>>) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result.get(0).get("foo")).isEqualTo(1);
        }

        @Test
        @DisplayName("Convert Map<String, ?> to field generic type Map<String, ?>[] (hidden url encoded array)")
        public void test1646783865319() {
            final Type genericType = MapPojo.PojoFields.ARRAY_MAP_OBJECT.getGenericType();
            final Map<String, Object> rawData = mapOf("foo", "1");
            final Map<String, Object>[] result =
                    (Map<String, Object>[]) marshaller().convertRawValueToTargetJavaType(rawData, genericType);
            assertThat(result[0].get("foo")).isEqualTo("1");
        }

        @Test
        @DisplayName("Convert Map<String, ?> to field generic type Map[] (hidden url encoded array)")
        public void test1646828259346() {
            final Type genericType = MapPojo.PojoFields.ARRAY_RAW_MAP.getGenericType();
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
            final Type genericType = MapPojo.PojoFields.MAP_OBJECT.getGenericType();
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
            final String fieldName = MapPojo.PojoFields.INTEGER.getFieldName();
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
            final String integerFieldName = Pojo.PojoFields.INTEGER.getFieldName();
            final String nestedPojoFieldName = Pojo.PojoFields.NESTED_POJO.getFieldName();
            final Type genericType = Pojo.PojoFields.NESTED_POJO.getGenericType();
            final Map<String, Object> rawData = mapOf(nestedPojoFieldName, mapOf(integerFieldName, "123"));
            assertThrow(() -> marshaller().convertRawValueToTargetJavaType(rawData, genericType))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Incompatible types received for conversion.\n" +
                                     "    Source value: {nestedPojo={integer=123}}\n" +
                                     "    Source type: java.util.HashMap\n" +
                                     "    Target type: " + POJO_TYPE_NAME + "\n");
        }

    }

    @Nested
    @DisplayName("#unmarshalTo method tests")
    public class UnmarshalToMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646935755648() {
            assertRequired(() -> marshaller().unmarshalTo(null, ""), "object");
            assertRequired(() -> marshaller().unmarshalTo(pojo(), null), "encodedString");
        }

        @Test
        @DisplayName("Unmarshal query string to Map")
        public void test1646927546174() {
            final HashMap<?, ?> map = new HashMap<>();
            marshaller().unmarshalTo(map, "foo[bar]=123&foo[car][0]=V0&foo[car][2]=" + ENCODED);
            assertThat(map).isNotEmpty();
            assertThat(map.get("foo")).isInstanceOf(Map.class);
            Map<String, Object> foo = (Map<String, Object>) map.get("foo");
            assertThat(foo.get("bar")).isEqualTo("123");
            assertThat(foo.get("car")).isInstanceOf(List.class);
            List<String> car = (List<String>) foo.get("car");
            assertThat(car.toString()).isEqualTo("[V0, null, " + DECODED + "]");
        }

        @Test
        @DisplayName("Unmarshal query string to Pojo")
        public void test1646928144418() {
            final Pojo pojo = pojo();
            marshaller().unmarshalTo(pojo, "nestedPojo[string]=1111111&" +
                                           "nestedPojo[integer]=2222222&" +
                                           "nestedPojo[listString][1]=33333333&" +
                                           "arrayString=44444444&" +
                                           "arrayString=55555555&" +
                                           "aaaaaaaaaaaaaaaaaa=" + ENCODED);
            assertThat(pojo.nestedPojo().string()).isEqualTo("1111111");
            assertThat(pojo.nestedPojo().integer()).isEqualTo(2222222);
            assertThat(pojo.nestedPojo().listString().toString()).isEqualTo("[null, 33333333]");
            assertThat(pojo.arrayString()[0]).isEqualTo("44444444");
            assertThat(pojo.arrayString()[1]).isEqualTo("55555555");
            assertThat(pojo.additionalProperties().toString()).isEqualTo("{aaaaaaaaaaaaaaaaaa=" + DECODED + "}");
        }

        @Test
        @DisplayName("Unmarshal query string to MapPojo")
        public void test1646932636257() {
            final MapPojo pojo = mapPojo();
            marshaller().unmarshalTo(pojo, "nestedMapPojo[string]=1111111&" +
                                           "nestedMapPojo[integer]=2222222&" +
                                           "nestedMapPojo[listString][1]=33333333&" +
                                           "arrayString=44444444&" +
                                           "arrayString=55555555&" +
                                           "aaaaaaaaaaaaaaaaaa=" + ENCODED);
            assertThat(pojo.nestedMapPojo().string()).isEqualTo("1111111");
            assertThat(pojo.nestedMapPojo().integer()).isEqualTo(2222222);
            assertThat(pojo.nestedMapPojo().listString().toString()).isEqualTo("[null, 33333333]");
            assertThat(pojo.arrayString()[0]).isEqualTo("44444444");
            assertThat(pojo.arrayString()[1]).isEqualTo("55555555");
            assertThat(pojo.additionalProperties().toString()).isEqualTo("{aaaaaaaaaaaaaaaaaa=" + DECODED + "}");
        }

        @Test
        @DisplayName("Unmarshal query string to EmptyPojo (prohibitAdditionalProperties=false (default))")
        public void test1646932940356() {
            EmptyPojo emptyPojo = new EmptyPojo();
            marshaller().unmarshalTo(emptyPojo, "foo=bar");
            assertThat(emptyPojo.foo).isNull();
        }

        @Test
        @DisplayName("Unmarshal query string to MapPojo without AP (prohibitAdditionalProperties=true (ignored))")
        public void test1646933638931() {
            MapPojoWithoutAdditionalProperties pojo = new MapPojoWithoutAdditionalProperties();
            marshaller().prohibitAdditionalProperties(true).unmarshalTo(pojo, "foo=bar");
            assertThat(pojo.get("foo")).isEqualTo("bar");
        }

        @Test
        @DisplayName("Unmarshal query string to Pojo (prohibitAdditionalProperties=true)")
        public void test1646933107631() {
            Pojo pojo = pojo();
            assertThrow(() -> marshaller().prohibitAdditionalProperties(true).unmarshalTo(pojo, "foo=bar"))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  URL encoded string contains unmapped additional properties.\n" +
                                     "    Actual: {foo=bar}\n" +
                                     "    Expected: There are no additional properties.\n");
        }

        @Test
        @DisplayName("Unmarshal query string to Pojo (prohibitAdditionalProperties=true)")
        public void test1646933378312() {
            EmptyPojo pojo = new EmptyPojo();
            assertThrow(() -> marshaller().prohibitAdditionalProperties(true).unmarshalTo(pojo, "foo=bar"))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  URL encoded string contains unmapped additional properties.\n" +
                                     "    Actual: {foo=bar}\n" +
                                     "    Expected: There are no additional properties.\n");
        }

        @Test
        @DisplayName("Unmarshal query string to MapPojo with AP (prohibitAdditionalProperties=true)")
        public void test1646933419223() {
            MapPojo pojo = mapPojo();
            assertThrow(() -> marshaller().prohibitAdditionalProperties(true).unmarshalTo(pojo, "foo=bar"))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  URL encoded string contains unmapped additional properties.\n" +
                                     "    Actual: {foo=bar}\n" +
                                     "    Expected: There are no additional properties.\n");
        }

        @Test
        @DisplayName("MarshallerException: Unexpected marshalling error")
        public void test1647101891000() {
            assertThrow(() -> marshaller().unmarshalTo(new BrokenMap<>(), "foo=bar"))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Unexpected unmarshalling error.\n" +
                                     "    Error cause:\n" +
                                     "     - UnsupportedOperationException: BrokenMap for test\n");
        }

    }

    @Nested
    @DisplayName("#unmarshal() method tests")
    public class UnmarshalMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646935806232() {
            assertRequired(() -> FormUrlMarshaller.INSTANCE.unmarshal(null, ""), "modelClass");
            assertRequired(() -> FormUrlMarshaller.INSTANCE.unmarshal(pojo().getClass(), null), "encodedString");
        }

        @Test
        @DisplayName("Unmarshal query string to Map")
        public void test1646935832984() {
            final HashMap<?, ?> map = marshaller().unmarshal(HashMap.class, "foo[bar]=123&" +
                                                                            "foo[car][0]=V0&" +
                                                                            "foo[car][2]=" + ENCODED);
            assertThat(map).isNotEmpty();
            assertThat(map.get("foo")).isInstanceOf(Map.class);
            Map<String, Object> foo = (Map<String, Object>) map.get("foo");
            assertThat(foo.get("bar")).isEqualTo("123");
            assertThat(foo.get("car")).isInstanceOf(List.class);
            List<String> car = (List<String>) foo.get("car");
            assertThat(car.toString()).isEqualTo("[V0, null, " + DECODED + "]");
        }

        @Test
        @DisplayName("Unmarshal query string to Pojo")
        public void test1646935845200() {
            final Pojo pojo = marshaller().unmarshal(Pojo.class, "nestedPojo[string]=1111111&" +
                                                                 "nestedPojo[integer]=2222222&" +
                                                                 "nestedPojo[listString][1]=33333333&" +
                                                                 "arrayString=44444444&" +
                                                                 "arrayString=55555555&" +
                                                                 "aaaaaaaaaaaaaaaaaa=" + ENCODED);
            assertThat(pojo.nestedPojo().string()).isEqualTo("1111111");
            assertThat(pojo.nestedPojo().integer()).isEqualTo(2222222);
            assertThat(pojo.nestedPojo().listString().toString()).isEqualTo("[null, 33333333]");
            assertThat(pojo.arrayString()[0]).isEqualTo("44444444");
            assertThat(pojo.arrayString()[1]).isEqualTo("55555555");
            assertThat(pojo.additionalProperties().toString()).isEqualTo("{aaaaaaaaaaaaaaaaaa=" + DECODED + "}");
        }

        @Test
        @DisplayName("MarshallerException: if model class is Map (interface)")
        public void test1646936491761() {
            assertThrow(() -> marshaller().unmarshal(Map.class, "foo=bar"))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Unable to instantiate model class.\n" +
                                     "    Source type: java.util.Map\n" +
                                     "    Error cause:\n" +
                                     "     - NoSuchMethodException: " +
                                     "No such accessible constructor on object: java.util.Map\n");
        }

        @Test
        @DisplayName("MarshallerException: if private constructor")
        public void test1646936771264() {
            final String typeName = PrivatePojo.class.getTypeName();
            assertThrow(() -> marshaller().unmarshal(PrivatePojo.class, "foo=bar"))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Unable to instantiate model class.\n" +
                                     "    Source type: " + typeName + "\n" +
                                     "    Error cause:\n" +
                                     "     - NoSuchMethodException:" +
                                     " No such accessible constructor on object: qa.model.PrivatePojo\n");
        }

        @Test
        @DisplayName("MarshallerException: if constructor with parameters")
        public void test1646936951388() {
            final String typeName = PojoWithConstructorParams.class.getTypeName();
            assertThrow(() -> marshaller().unmarshal(PojoWithConstructorParams.class, "foo=bar"))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Unable to instantiate model class.\n" +
                                     "    Source type: " + typeName + "\n" +
                                     "    Error cause:\n" +
                                     "     - NoSuchMethodException: No such accessible constructor on object:" +
                                     " qa.model.PojoWithConstructorParams\n");
        }

        @Test
        @DisplayName("MarshallerException: if constructor with parameters")
        public void test1647027713832() {
            assertThrow(() -> marshaller().unmarshal(ArrayList.class, "foo=bar"))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Received unsupported type for conversion.\n" +
                                     "    Actual type: java.util.ArrayList\n" +
                                     "    Expected: POJO classes with @FormUrlEncoded annotation\n" +
                                     "    Expected: heirs of java.util.Map\n");
        }

        @Test
        @DisplayName("MarshallerException: Unexpected marshalling error")
        public void test1647101891000() {
            assertThrow(() -> marshaller().unmarshal(BrokenMap.class, "foo=bar"))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Unexpected unmarshalling error.\n" +
                                     "    Error cause:\n" +
                                     "     - UnsupportedOperationException: BrokenMap for test\n");
        }

    }

    @Nested
    @DisplayName("#convertUrlDecodedStringValueToSimpleType() method tests")
    public class ConvertUrlDecodedStringValueToSimpleTypeMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645376480179() {
            assertRequired(() -> marshaller().convertUrlDecodedStringValueToSimpleType("", null), "targetType");
        }

        @Test
        @DisplayName("Successfully conversion string value to String type")
        public void test1645376586652() {
            final Object result = marshaller().convertUrlDecodedStringValueToSimpleType("qa", String.class);
            assertIs(result, "qa");
        }

        @Test
        @DisplayName("Successfully conversion string value to Object type")
        public void test1645377510696() {
            final Object result = marshaller().convertUrlDecodedStringValueToSimpleType("qa", Object.class);
            assertIs(result, "qa");
        }

        @Test
        @DisplayName("Successfully conversion integer string value to Integer type")
        public void test1645376667247() {
            final Object result = marshaller().convertUrlDecodedStringValueToSimpleType("1", Integer.class);
            assertIs(result, 1);
        }

        @Test
        @DisplayName("Successfully conversion integer string value to BigInteger type")
        public void test1645376817230() {
            final Object result = marshaller().convertUrlDecodedStringValueToSimpleType("1", BigInteger.class);
            assertIs(result, BigInteger.valueOf(1L));
        }

        @Test
        @DisplayName("Successfully conversion long string value to Long type")
        public void test1645376695492() {
            final Object result = marshaller().convertUrlDecodedStringValueToSimpleType("1", Long.class);
            assertIs(result, 1L);
        }

        @Test
        @DisplayName("Successfully conversion short string value to Short type")
        public void test1645376718177() {
            final Object result = marshaller().convertUrlDecodedStringValueToSimpleType("1", Short.class);
            assertIs(result, Short.valueOf("1"));
        }

        @Test
        @DisplayName("Successfully conversion float string value to Float type")
        public void test1645376750074() {
            final Object result = marshaller().convertUrlDecodedStringValueToSimpleType("0.1", Float.class);
            assertIs(result, 0.1F);
        }

        @Test
        @DisplayName("Successfully conversion double string value to Double type")
        public void test1645376788850() {
            final Object result = marshaller().convertUrlDecodedStringValueToSimpleType("0.1", Double.class);
            assertIs(result, 0.1);
        }

        @Test
        @DisplayName("Successfully conversion integer string value to BigDecimal type")
        public void test1645376867326() {
            final Object result = marshaller().convertUrlDecodedStringValueToSimpleType("0.1", BigDecimal.class);
            assertIs(result, BigDecimal.valueOf(0.1));
        }

        @Test
        @DisplayName("Successfully conversion 'true' string value to Boolean type")
        public void test1645376909313() {
            final Object result = marshaller().convertUrlDecodedStringValueToSimpleType("true", Boolean.class);
            assertIs(result, true);
        }

        @Test
        @DisplayName("Successfully conversion 'false' string value to Boolean type")
        public void test1645376924701() {
            final Object result = marshaller().convertUrlDecodedStringValueToSimpleType("false", Boolean.class);
            assertIs(result, false);
        }

        @Test
        @DisplayName("Successfully conversion null value to null")
        public void test1647026260894() {
            final Object result = marshaller().convertUrlDecodedStringValueToSimpleType(null, Boolean.class);
            assertIs(result, null);
        }

        @Test
        @DisplayName("MarshallerException -> conversion 'FooBar' string value to Boolean type")
        public void test1645377271047() {
            assertThrow(() -> marshaller().convertUrlDecodedStringValueToSimpleType("FooBar", Boolean.class))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Incompatible types received for conversion.\n" +
                                     "    Source type: java.lang.String\n" +
                                     "    Source value: FooBar\n" +
                                     "    Target type: java.lang.Boolean\n" +
                                     "    Expected value: true || false\n");
        }

        @Test
        @DisplayName("MarshallerException -> conversion 'FooBar' string value to BigInteger type")
        public void test1647102344583() {
            assertThrow(() -> marshaller().convertUrlDecodedStringValueToSimpleType("FooBar", BigInteger.class))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Incompatible types received for conversion.\n" +
                                     "    Source type: java.lang.String\n" +
                                     "    Source value: FooBar\n" +
                                     "    Target type: java.math.BigInteger\n" +
                                     "    Error cause:\n" +
                                     "     - NumberFormatException: For input string: \"FooBar\"\n");
        }


        @Test
        @DisplayName("MarshallerException -> field type is primitive")
        public void test1645377119906() {
            assertThrow(() -> marshaller().convertUrlDecodedStringValueToSimpleType("false", Boolean.TYPE))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Forbidden to use primitive types\n" +
                                     "    Actual type: boolean\n" +
                                     "    Expected: simple reference types (String, Integer, Boolean, etc.)\n");
        }

        @Test
        @DisplayName("MarshallerException -> unsupported field type (Map class)")
        public void test1645377473744() {
            assertThrow(() -> marshaller().convertUrlDecodedStringValueToSimpleType("false", Map.class))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Received unsupported type for conversion.\n" +
                                     "    Actual type: java.util.Map\n" +
                                     "    Expected: simple reference types (String, Integer, Boolean, etc.)\n");
        }

        @Test
        @DisplayName("MarshallerException -> unsupported field type (List generic type)")
        public void test1647097912238() {
            final Type genericType = Pojo.PojoFields.LIST_STRING.getGenericType();
            assertThrow(() -> marshaller().convertUrlDecodedStringValueToSimpleType("false", genericType))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Received unsupported type for conversion.\n" +
                                     "    Actual type: java.util.List<java.lang.String>\n" +
                                     "    Expected: simple reference types (String, Integer, Boolean, etc.)\n");
        }

    }

    @Nested
    @DisplayName("#initAdditionalProperties() method tests")
    public class InitAdditionalPropertiesMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645292235482() throws NoSuchFieldException {
            final Pojo model = pojo();
            final Field field = Pojo.class.getDeclaredField("additionalProperties");
            assertRequired(() -> marshaller().getAdditionalProperties(null, field), "model");
            assertRequired(() -> marshaller().getAdditionalProperties(model, null), "field");
        }

        @Test
        @DisplayName("Successfully getting additionalProperties map if field initiated")
        public void test1645292268987() throws NoSuchFieldException {
            final AdditionalProperties model = new AdditionalProperties();
            model.additionalProperties = new HashMap<>();
            model.additionalProperties.put("1", "2");
            final Field field = AdditionalProperties.class.getDeclaredField("additionalProperties");
            final Map<Object, Object> result = marshaller().getAdditionalProperties(model, field);
            assertIs(result, model.additionalProperties);
        }

        @Test
        @DisplayName("Successfully getting additionalProperties map if field not initiated")
        public void test1645292357593() throws NoSuchFieldException {
            final Field field = AdditionalProperties.class.getDeclaredField("additionalProperties");
            final Map<Object, Object> result = marshaller().getAdditionalProperties(new AdditionalProperties(), field);
            assertIs(result, new HashMap<>());
        }

        @Test
        @DisplayName("Successfully getting additionalProperties map if field initiated (final)")
        public void test1645292517696() throws NoSuchFieldException {
            final AdditionalPropertiesFinal model = new AdditionalPropertiesFinal();
            model.additionalProperties.put("1", "2");
            final Field field = AdditionalPropertiesFinal.class.getDeclaredField("additionalProperties");
            final Map<Object, Object> result = marshaller().getAdditionalProperties(model, field);
            assertIs(result, model.additionalProperties);
        }

        @Test
        @DisplayName("MarshallerException if unable to initialize additionalProperties field")
        public void test1645292405815() throws NoSuchFieldException {
            final Field field = AdditionalProperties.class.getDeclaredField("additionalProperties");
            assertThrow(() -> marshaller().getAdditionalProperties(new EmptyPojo(), field))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Unable to raed value from object field.\n" +
                                     "    Model: qa.model.EmptyPojo\n" +
                                     "    Field: public Map<String, Object> additionalProperties;\n" +
                                     "    Error cause:\n" +
                                     "     - IllegalArgumentException: Cannot locate field additionalProperties" +
                                     " on class qa.model.EmptyPojo\n");
        }

        @Test
        @DisplayName("MarshallerException if additionalProperty field not readable")
        public void test1645458156865() throws NoSuchFieldException {
            final Field field = AdditionalPropertiesFinal.class.getDeclaredField("additionalProperties");
            assertThrow(() -> marshaller().getAdditionalProperties(new EmptyPojo(), field))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Unable to raed value from object field.\n" +
                                     "    Model: qa.model.EmptyPojo\n" +
                                     "    Field: public final Map<String, Object> additionalProperties;\n" +
                                     "    Error cause:\n" +
                                     "     - IllegalArgumentException: Cannot locate field additionalProperties" +
                                     " on class qa.model.EmptyPojo\n");
        }
    }

    @Nested
    @DisplayName("#getAdditionalPropertiesField() method tests")
    public class GetAdditionalPropertiesFieldMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1645279093093() {
            assertRequired(() -> marshaller().getAdditionalPropertiesField(null), "modelClass");
        }

        @Test
        @DisplayName("Successfully getting additionalProperties field from a class (contains annotation)")
        public void test1645281367804() {
            final Field additionalProperties = marshaller().getAdditionalPropertiesField(AdditionalProperties.class);
            assertNotNull(additionalProperties);
        }

        @Test
        @DisplayName("Successfully getting additionalProperties field (null) from a class (without annotation)")
        public void test1645281802803() {
            final Field additionalProperties = marshaller().getAdditionalPropertiesField(AdditionalPropertiesWithoutAnnotation.class);
            assertIsNull(additionalProperties);
        }

        @Test
        @DisplayName("Successfully getting additionalProperties field (null) from a class (empty class)")
        public void test1645281893559() {
            final Field additionalProperties = marshaller().getAdditionalPropertiesField(EmptyPojo.class);
            assertIsNull(additionalProperties);
        }

        @Test
        @DisplayName("MarshallerException throws if additionalProperties type == Map<String, Integer>")
        public void test1645282054759() {
            assertThrow(() -> marshaller().getAdditionalPropertiesField(AdditionalPropertiesStringInteger.class))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Invalid additional properties field type\n" +
                                     "    Model: qa.model.AdditionalPropertiesStringInteger\n" +
                                     "    Field: private Map<String, Integer> additionalProperties;\n" +
                                     "    Actual type: java.util.Map<java.lang.String, java.lang.Integer>\n" +
                                     "    Expected: java.util.Map<java.lang.String, java.lang.Object>\n");
        }

        @Test
        @DisplayName("MarshallerException throws if additionalProperties type == Map<?, ?>")
        public void test1647098952875() {
            assertThrow(() -> marshaller().getAdditionalPropertiesField(AdditionalPropertiesRawGeneric.class))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Invalid additional properties field type\n" +
                                     "    Model: qa.model.AdditionalPropertiesRawGeneric\n" +
                                     "    Field: public Map<?, ?> additionalProperties;\n" +
                                     "    Actual type: java.util.Map<?, ?>\n" +
                                     "    Expected: java.util.Map<java.lang.String, java.lang.Object>\n");
        }


        @Test
        @DisplayName("MarshallerException throws if additionalProperties type == Map<Integer, String>")
        public void test1647098821433() {
            assertThrow(() -> marshaller().getAdditionalPropertiesField(AdditionalPropertiesIntegerString.class))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Invalid additional properties field type\n" +
                                     "    Model: qa.model.AdditionalPropertiesIntegerString\n" +
                                     "    Field: private Map<Integer, String> additionalProperties;\n" +
                                     "    Actual type: java.util.Map<java.lang.Integer, java.lang.String>\n" +
                                     "    Expected: java.util.Map<java.lang.String, java.lang.Object>\n");
        }

        @Test
        @DisplayName("MarshallerException throws if additionalProperties type == Map<Integer, Map<String, Map<String, List<Integer>>>>")
        public void test1647099813100() {
            assertThrow(() -> marshaller().getAdditionalPropertiesField(AdditionalPropertiesStringMapListInteger.class))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Invalid additional properties field type\n" +
                                     "    Model: qa.model.AdditionalPropertiesStringMapListInteger\n" +
                                     "    Field: private Map<String, Map<String, List<Integer>>> additionalProperties;\n" +
                                     "    Actual type: java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>>\n" +
                                     "    Expected: java.util.Map<java.lang.String, java.lang.Object>\n");
        }

        @Test
        @DisplayName("MarshallerException throws if additionalProperties type == Map (raw type)")
        public void test1645282252989() {
            assertThrow(() -> marshaller().getAdditionalPropertiesField(AdditionalPropertiesRawMap.class))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Invalid additional properties field type\n" +
                                     "    Model: qa.model.AdditionalPropertiesRawMap\n" +
                                     "    Field: private Map additionalProperties;\n" +
                                     "    Actual type: java.util.Map\n" +
                                     "    Expected: java.util.Map<java.lang.String, java.lang.Object>\n");
        }

        @Test
        @DisplayName("MarshallerException throws if additionalProperties type == List<String>")
        public void test1645282363009() {
            assertThrow(() -> marshaller().getAdditionalPropertiesField(AdditionalPropertiesList.class))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Invalid additional properties field type\n" +
                                     "    Model: qa.model.AdditionalPropertiesList\n" +
                                     "    Field: private List<String> additionalProperties;\n" +
                                     "    Actual type: java.util.List<java.lang.String>\n" +
                                     "    Expected: java.util.Map<java.lang.String, java.lang.Object>\n");
        }

        @Test
        @DisplayName("MarshallerException throws if several additionalProperties fields")
        public void test1645282588975() {
            assertThrow(() -> marshaller().getAdditionalPropertiesField(AdditionalPropertiesSeveralFields.class))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  POJO contains more than one annotated fields.\n" +
                                     "    Model: qa.model.AdditionalPropertiesSeveralFields\n" +
                                     "    Annotation: @FormUrlEncodedAdditionalProperties\n" +
                                     "    Annotation type: org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties\n" +
                                     "    Fields:\n" +
                                     "     - private Map<String, Object> additionalProperties1;\n" +
                                     "     - private Map<String, Object> additionalProperties2;\n" +
                                     "    Expected: one annotated field\n");
        }

    }

    private static FormUrlMarshaller marshaller() {
        return new FormUrlMarshaller();
    }

}
