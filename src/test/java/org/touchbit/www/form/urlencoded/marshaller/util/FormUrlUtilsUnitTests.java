package org.touchbit.www.form.urlencoded.marshaller.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.www.form.urlencoded.marshaller.BaseTest;
import org.touchbit.www.form.urlencoded.marshaller.chain.IChainList;
import qa.model.GenericPojo;
import qa.model.MapPojo;
import qa.model.Pojo;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static qa.model.Pojo.PojoFields;

@SuppressWarnings({"ConstantConditions", "RedundantCast"})
@DisplayName("FormUrlUtils.class unit tests")
public class FormUrlUtilsUnitTests extends BaseTest {

    @Nested
    @DisplayName("#getGenericCollectionArgumentType() method tests")
    public class GetGenericCollectionArgumentTypeMethodTests {

        @Test
        @DisplayName("getGenericCollectionArgumentType")
        public void test1647037509034() {
            final Type type = FormUrlUtils.getGenericCollectionArgumentType(PojoFields.LIST_POJO.getGenericType());
            assertThat(type).isEqualTo(Pojo.class);
        }

        @Test
        @DisplayName("MarshallerException if type = null")
        public void test1647037621529() {
            assertThrow(() -> FormUrlUtils.getGenericCollectionArgumentType(null))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Received type is not a generic collection.\n" +
                                     "    Expected: heirs of java.lang.reflect.ParameterizedType\n" +
                                     "    Actual type: null\n");
        }

        @Test
        @DisplayName("MarshallerException if type = String[].class")
        public void test1647037988208() {
            assertThrow(() -> FormUrlUtils.getGenericCollectionArgumentType(arrayOf("1", "2").getClass()))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Received type is not a generic collection.\n" +
                                     "    Expected: heirs of java.lang.reflect.ParameterizedType\n" +
                                     "    Actual type: java.lang.String[]\n");
        }

        @Test
        @DisplayName("MarshallerException if type = Map.class")
        public void test1647037639266() {
            assertThrow(() -> FormUrlUtils.getGenericCollectionArgumentType(PojoFields.MAP_POJO.getGenericType()))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Incorrect number of TypeArguments was received for a generic collection.\n" +
                                     "    Actual type: java.util.Map<java.lang.String, qa.model.Pojo>\n" +
                                     "    Actual: 2 generic parameters\n" +
                                     "    Expected: 1 generic parameter\n");
        }


    }

    @Nested
    @DisplayName("#getArrayComponentType() method tests")
    public class GetArrayComponentTypeMethodTests {

        @Test
        @DisplayName("return Integer if type = Integer[]")
        public void test1647035897768() {
            final Type genericType = PojoFields.ARRAY_INTEGER.getGenericType();
            assertIs(FormUrlUtils.getArrayComponentType(genericType), Integer.class);
        }

        @Test
        @DisplayName("return Pojo if type = Pojo[]")
        public void test1647036020760() {
            final Type genericType = PojoFields.ARRAY_POJO.getGenericType();
            assertIs(FormUrlUtils.getArrayComponentType(genericType), Pojo.class);
        }

        @Test
        @DisplayName("return Map<String, Object> if type = Map<String, Object>[]")
        public void test1647036055105() {
            final Type genericType = PojoFields.ARRAY_MAP_OBJECT.getGenericType();
            assertIs(FormUrlUtils.getArrayComponentType(genericType).toString(), "java.util.Map<java.lang.String, java.lang.Object>");
        }

        @Test
        @DisplayName("MarshallerException if type = null")
        public void test1647036150292() {
            assertThrow(() -> FormUrlUtils.getArrayComponentType(null))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Type is not an array.\n" +
                                     "    Actual type: null\n" +
                                     "    Expected: array type\n");
        }

        @Test
        @DisplayName("MarshallerException if type = Object.class")
        public void test1647036159983() {
            assertThrow(() -> FormUrlUtils.getArrayComponentType(Object.class))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Type is not an array.\n" +
                                     "    Actual type: java.lang.Object\n" +
                                     "    Expected: array type\n");
        }

        @Test
        @DisplayName("MarshallerException if type = List.class")
        public void test1647036284496() {
            assertThrow(() -> FormUrlUtils.getArrayComponentType(ArrayList.class))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Type is not an array.\n" +
                                     "    Actual type: java.util.ArrayList\n" +
                                     "    Expected: array type\n");
        }

    }

    @Nested
    @DisplayName("#encode() method tests")
    public class EncodeMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646678004644() {
            assertRequired(() -> FormUrlUtils.encode(null, UTF_8), "value");
        }

        @Test
        @DisplayName("Encode decoded string")
        public void test1646678016784() {
            assertThat(FormUrlUtils.encode(DECODED, UTF_8)).isEqualTo(ENCODED);
        }

    }

    @Nested
    @DisplayName("#decode() method tests")
    public class DecodeMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646937184822() {
            assertRequired(() -> FormUrlUtils.decode(null, UTF_8), "value");
        }

        @Test
        @DisplayName("Decode encoded string")
        public void test1646677890242() {
            assertThat(FormUrlUtils.decode(ENCODED, UTF_8)).isEqualTo(DECODED);
        }

    }

    @Nested
    @DisplayName("#readField() method tests")
    public class ReadFieldMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1647029094362() {
            assertRequired(() -> FormUrlUtils.readField(null, PojoFields.INTEGER.getDeclaredField()), "object");
            assertRequired(() -> FormUrlUtils.readField(new Object(), null), "field");
        }

        @Test
        @DisplayName("read field value")
        public void test1647029190803() {
            final Pojo pojo = pojo().integer(100500);
            final Field field = PojoFields.INTEGER.getDeclaredField();
            final Object o = FormUrlUtils.readField(pojo, field);
            assertThat(o).isEqualTo(100500);
        }

        @Test
        @DisplayName("MarshallerException if field not readable")
        public void test1647029300652() {
            final Field field = PojoFields.INTEGER.getDeclaredField();
            assertThrow(() -> FormUrlUtils.readField(new HashMap<>(), field))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Unable to raed value from object field.\n" +
                                     "    Model: java.util.HashMap\n" +
                                     "    Field: private Integer integer;\n" +
                                     "    Error cause: Cannot locate field integer on class java.util.HashMap\n");
        }

    }

    @Nested
    @DisplayName("#writeDeclaredField() method tests")
    public class WriteDeclaredFieldMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1647029387335() {
            final Field field = PojoFields.INTEGER.getDeclaredField();
            assertRequired(() -> FormUrlUtils.writeDeclaredField(null, field, ""), "object");
            assertRequired(() -> FormUrlUtils.writeDeclaredField(new HashMap<>(), null, ""), "field");
            assertRequired(() -> FormUrlUtils.writeDeclaredField(new HashMap<>(), field, null), "value");
        }

        @Test
        @DisplayName("write field value")
        public void test1647029478110() {
            final Field field = PojoFields.INTEGER.getDeclaredField();
            final Pojo pojo = pojo();
            FormUrlUtils.writeDeclaredField(pojo, field, 123123);
            assertThat(pojo.integer()).isEqualTo(123123);
        }

        @Test
        @DisplayName("MarshallerException if field not writable")
        public void test1647029531978() {
            final Field field = PojoFields.INTEGER.getDeclaredField();
            final Pojo pojo = pojo();
            assertThrow(() -> FormUrlUtils.writeDeclaredField(pojo, field, "test"))
                    .assertClass(MarshallerException.class)
                    .assertMessageIs("\n  Unable to write value to object field.\n" +
                                     "    Model: qa.model.Pojo\n" +
                                     "    Field: private Integer integer;\n" +
                                     "    Value: test\n" +
                                     "    Value type: java.lang.String\n" +
                                     "    Error cause: " +
                                     "Can not set java.lang.Integer field qa.model.Pojo.integer to java.lang.String\n");
        }

    }

    @Test
    @DisplayName("#isGenericMap() method tests")
    public void test1647029668029() {
        assertFalse(FormUrlUtils.isGenericMap(null));
        assertFalse(FormUrlUtils.isGenericMap(PojoFields.INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isGenericMap(PojoFields.ARRAY_RAW_MAP.getGenericType()));
        assertFalse(FormUrlUtils.isGenericMap(PojoFields.MAP_RAW.getGenericType()));
        assertFalse(FormUrlUtils.isGenericMap(PojoFields.LIST_MAP_STRING_INTEGER.getGenericType()));

        assertTrue(FormUrlUtils.isGenericMap(PojoFields.MAP_RAW_GENERIC.getGenericType()));
        assertTrue(FormUrlUtils.isGenericMap(PojoFields.MAP_MAP_INTEGER.getGenericType()));
    }

    @Test
    @DisplayName("isGenericCollection")
    public void test1647030035703() {
        assertFalse(FormUrlUtils.isGenericCollection(null));
        assertFalse(FormUrlUtils.isGenericCollection(PojoFields.INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isGenericCollection(PojoFields.ARRAY_RAW_MAP.getGenericType()));
        assertFalse(FormUrlUtils.isGenericCollection(PojoFields.MAP_RAW.getGenericType()));
        assertFalse(FormUrlUtils.isGenericCollection(PojoFields.MAP_RAW_GENERIC.getGenericType()));
        assertFalse(FormUrlUtils.isGenericCollection(PojoFields.MAP_MAP_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isGenericCollection(PojoFields.LIST_RAW.getGenericType()));

        assertTrue(FormUrlUtils.isGenericCollection(PojoFields.LIST_MAP_STRING_INTEGER.getGenericType()));
        assertTrue(FormUrlUtils.isGenericCollection(PojoFields.LIST_RAW_GENERIC.getGenericType()));
        assertTrue(FormUrlUtils.isGenericCollection(PojoFields.LIST_LIST_STRING.getGenericType()));
    }

    @Test
    @DisplayName("isArray")
    public void test1647030516440() {
        assertFalse(FormUrlUtils.isArray((Object) null));
        assertFalse(FormUrlUtils.isArray((Type) null));
        assertFalse(FormUrlUtils.isArray(PojoFields.INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isArray(PojoFields.MAP_RAW.getGenericType()));
        assertFalse(FormUrlUtils.isArray(PojoFields.MAP_RAW_GENERIC.getGenericType()));
        assertFalse(FormUrlUtils.isArray(PojoFields.MAP_MAP_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isArray(PojoFields.LIST_RAW.getGenericType()));
        assertFalse(FormUrlUtils.isArray(PojoFields.LIST_ARRAY_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isArray(PojoFields.ARRAY_MAP_OBJECT.getGenericType()));

        assertTrue(FormUrlUtils.isArray(PojoFields.ARRAY_RAW_MAP.getGenericType()));
        assertTrue(FormUrlUtils.isArray(PojoFields.ARRAY_POJO.getGenericType()));
        assertTrue(FormUrlUtils.isArray(PojoFields.ARRAY_INTEGER.getGenericType()));
    }

    @Test
    @DisplayName("isGenericArray")
    public void test1647030614301() {
        assertFalse(FormUrlUtils.isGenericArray(null));
        assertFalse(FormUrlUtils.isGenericArray(PojoFields.INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isGenericArray(PojoFields.MAP_RAW.getGenericType()));
        assertFalse(FormUrlUtils.isGenericArray(PojoFields.MAP_RAW_GENERIC.getGenericType()));
        assertFalse(FormUrlUtils.isGenericArray(PojoFields.MAP_MAP_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isGenericArray(PojoFields.LIST_RAW.getGenericType()));
        assertFalse(FormUrlUtils.isGenericArray(PojoFields.LIST_ARRAY_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isGenericArray(PojoFields.ARRAY_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isGenericArray(PojoFields.ARRAY_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isGenericArray(PojoFields.ARRAY_RAW_MAP.getGenericType()));

        assertTrue(FormUrlUtils.isGenericArray(PojoFields.ARRAY_MAP_OBJECT.getGenericType()));
        assertTrue(FormUrlUtils.isGenericArray(PojoFields.ARRAY_LIST_OBJECT.getGenericType()));
    }

    @Test
    @DisplayName("getParameterizedType")
    public void test1647030995417() {
        assertIsNull(FormUrlUtils.getParameterizedType(null));
        assertIsNull(FormUrlUtils.getParameterizedType(PojoFields.INTEGER.getGenericType()));
        assertIsNull(FormUrlUtils.getParameterizedType(PojoFields.MAP_RAW.getGenericType()));
        assertIsNull(FormUrlUtils.getParameterizedType(PojoFields.ARRAY_INTEGER.getGenericType()));
        assertIsNull(FormUrlUtils.getParameterizedType(PojoFields.ARRAY_POJO.getGenericType()));
        assertIsNull(FormUrlUtils.getParameterizedType(PojoFields.ARRAY_RAW_MAP.getGenericType()));
        assertIsNull(FormUrlUtils.getParameterizedType(PojoFields.ARRAY_MAP_OBJECT.getGenericType()));
        assertIsNull(FormUrlUtils.getParameterizedType(PojoFields.ARRAY_LIST_OBJECT.getGenericType()));
        assertIsNull(FormUrlUtils.getParameterizedType(PojoFields.LIST_RAW.getGenericType()));

        assertNotNull(FormUrlUtils.getParameterizedType(PojoFields.LIST_ARRAY_INTEGER.getGenericType()));
        assertNotNull(FormUrlUtils.getParameterizedType(PojoFields.LIST_RAW_GENERIC.getGenericType()));
        assertNotNull(FormUrlUtils.getParameterizedType(PojoFields.MAP_RAW_GENERIC.getGenericType()));
        assertNotNull(FormUrlUtils.getParameterizedType(PojoFields.MAP_MAP_INTEGER.getGenericType()));
    }

    @Test
    @DisplayName("isCollection")
    public void test1647032193467() {
        assertFalse(FormUrlUtils.isCollection(null));
        assertFalse(FormUrlUtils.isCollection(PojoFields.INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isCollection(PojoFields.MAP_RAW.getGenericType()));
        assertFalse(FormUrlUtils.isCollection(PojoFields.ARRAY_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isCollection(PojoFields.ARRAY_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollection(PojoFields.ARRAY_RAW_MAP.getGenericType()));
        assertFalse(FormUrlUtils.isCollection(PojoFields.ARRAY_MAP_OBJECT.getGenericType()));
        assertFalse(FormUrlUtils.isCollection(PojoFields.ARRAY_LIST_OBJECT.getGenericType()));
        assertFalse(FormUrlUtils.isCollection(PojoFields.MAP_RAW_GENERIC.getGenericType()));
        assertFalse(FormUrlUtils.isCollection(PojoFields.MAP_MAP_INTEGER.getGenericType()));

        assertTrue(FormUrlUtils.isCollection(PojoFields.LIST_POJO.getGenericType()));
        assertTrue(FormUrlUtils.isCollection(PojoFields.LIST_RAW.getGenericType()));
        assertTrue(FormUrlUtils.isCollection(PojoFields.LIST_OBJECT.getGenericType()));
        assertTrue(FormUrlUtils.isCollection(PojoFields.LIST_RAW_GENERIC.getGenericType()));
        assertTrue(FormUrlUtils.isCollection(PojoFields.LIST_LIST_STRING.getGenericType()));
        assertTrue(FormUrlUtils.isCollection(PojoFields.LIST_ARRAY_INTEGER.getGenericType()));
    }

    @Test
    @DisplayName("isChainList")
    public void test1647032598648() {
        assertTrue(FormUrlUtils.isChainList(new IChainList.Default(true)));
        assertTrue(FormUrlUtils.isChainList(new IChainList.Default(false)));
        assertFalse(FormUrlUtils.isChainList(new ArrayList<>()));
    }

    @Test
    @DisplayName("isMapAssignableFrom")
    public void test1647032907309() {
        assertFalse(FormUrlUtils.isMapAssignableFrom(null));
        assertFalse(FormUrlUtils.isMapAssignableFrom(PojoFields.INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isMapAssignableFrom(PojoFields.MAP_OBJECT.getGenericType()));
        assertFalse(FormUrlUtils.isMapAssignableFrom(PojoFields.MAP_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isMapAssignableFrom(PojoFields.MAP_MAP_STRING.getGenericType()));
        assertFalse(FormUrlUtils.isMapAssignableFrom(PojoFields.MAP_MAP_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isMapAssignableFrom(PojoFields.MAP_RAW_GENERIC.getGenericType()));

        assertTrue(FormUrlUtils.isMapAssignableFrom(PojoFields.MAP_RAW.getGenericType()));
        assertTrue(FormUrlUtils.isMapAssignableFrom(new HashMap<>()));
    }

    @Test
    @DisplayName("isPojo")
    public void test1647033936792() {
        assertFalse(FormUrlUtils.isPojo(null));
        assertFalse(FormUrlUtils.isPojo(PojoFields.INTEGER.getGenericType()));

        assertTrue(FormUrlUtils.isPojo(PojoFields.NESTED_POJO.getGenericType()));
        assertTrue(FormUrlUtils.isPojo(MapPojo.PojoFields.NESTED_MAP_POJO.getGenericType()));
        assertTrue(FormUrlUtils.isPojo(GenericPojo.PojoFields.NESTED_GENERIC_POJO.getGenericType()));
        assertTrue(FormUrlUtils.isPojo(new Pojo()));
        assertTrue(FormUrlUtils.isPojo(new MapPojo()));
        assertTrue(FormUrlUtils.isPojo(new GenericPojo<>()));
    }

    @Test
    @DisplayName("isSomePojo")
    public void test1647034676470() {
        assertFalse(FormUrlUtils.isSomePojo(null));
        assertFalse(FormUrlUtils.isSomePojo(PojoFields.INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isSomePojo(PojoFields.MAP_POJO.getGenericType())); // Map<String, POJO> - false

        assertTrue(FormUrlUtils.isSomePojo(new Pojo()));
        assertTrue(FormUrlUtils.isSomePojo(new MapPojo()));
        assertTrue(FormUrlUtils.isSomePojo(new GenericPojo<>()));
        assertTrue(FormUrlUtils.isSomePojo(PojoFields.NESTED_POJO.getGenericType()));
        assertTrue(FormUrlUtils.isSomePojo(PojoFields.ARRAY_POJO.getGenericType()));
        assertTrue(FormUrlUtils.isSomePojo(PojoFields.LIST_POJO.getGenericType()));
    }

    @Test
    @DisplayName("isPojoArray")
    public void test1647034769926() {
        assertFalse(FormUrlUtils.isPojoArray(null));
        assertFalse(FormUrlUtils.isPojoArray(PojoFields.INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isPojoArray(new MapPojo()));
        assertFalse(FormUrlUtils.isPojoArray(new GenericPojo<>()));
        assertFalse(FormUrlUtils.isPojoArray(PojoFields.NESTED_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isPojoArray(PojoFields.MAP_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isPojoArray(PojoFields.LIST_POJO.getGenericType()));

        assertTrue(FormUrlUtils.isPojoArray(new Pojo[]{}));
        assertTrue(FormUrlUtils.isPojoArray(PojoFields.ARRAY_POJO.getGenericType()));
    }

    @Test
    @DisplayName("isPojoGenericCollection")
    public void test1647035450352() {
        assertFalse(FormUrlUtils.isPojoGenericCollection(null));
        assertFalse(FormUrlUtils.isPojoGenericCollection(PojoFields.INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isPojoGenericCollection(new MapPojo()));
        assertFalse(FormUrlUtils.isPojoGenericCollection(new GenericPojo<>()));
        assertFalse(FormUrlUtils.isPojoGenericCollection(PojoFields.NESTED_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isPojoGenericCollection(PojoFields.MAP_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isPojoGenericCollection(new Pojo[]{}));
        assertFalse(FormUrlUtils.isPojoGenericCollection(new ArrayList<>()));
        assertFalse(FormUrlUtils.isPojoGenericCollection(PojoFields.ARRAY_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isPojoGenericCollection(PojoFields.LIST_LIST_STRING.getGenericType()));
        assertFalse(FormUrlUtils.isPojoGenericCollection(PojoFields.LIST_LIST_POJO.getGenericType()));

        assertTrue(FormUrlUtils.isPojoGenericCollection(PojoFields.LIST_POJO.getGenericType()));
    }

    @Test
    @DisplayName("isSimple")
    public void test1647037255026() {
        assertFalse(FormUrlUtils.isSimple(new Object()));
        assertFalse(FormUrlUtils.isSimple((Object) null));
        assertFalse(FormUrlUtils.isSimple(Object.class));
        assertFalse(FormUrlUtils.isSimple((Type) null));

        assertTrue(FormUrlUtils.isSimple(String.class));
        assertTrue(FormUrlUtils.isSimple(""));
        assertTrue(FormUrlUtils.isSimple(Boolean.class));
        assertTrue(FormUrlUtils.isSimple(true));
        assertTrue(FormUrlUtils.isSimple(Short.class));
        assertTrue(FormUrlUtils.isSimple(Short.valueOf("1")));
        assertTrue(FormUrlUtils.isSimple(Long.class));
        assertTrue(FormUrlUtils.isSimple(1L));
        assertTrue(FormUrlUtils.isSimple(Float.class));
        assertTrue(FormUrlUtils.isSimple(1f));
        assertTrue(FormUrlUtils.isSimple(Integer.class));
        assertTrue(FormUrlUtils.isSimple(1));
        assertTrue(FormUrlUtils.isSimple(Double.class));
        assertTrue(FormUrlUtils.isSimple(1.1));
        assertTrue(FormUrlUtils.isSimple(BigInteger.class));
        assertTrue(FormUrlUtils.isSimple(new BigInteger("1")));
        assertTrue(FormUrlUtils.isSimple(BigDecimal.class));
        assertTrue(FormUrlUtils.isSimple(new BigDecimal(1)));
    }

    @Test
    @DisplayName("isCollectionOfSimpleObj")
    public void test1647092864496() {
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(null));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(new MapPojo()));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(new GenericPojo<>()));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(new Pojo[]{}));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.NESTED_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.MAP_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.ARRAY_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.LIST_LIST_STRING.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.LIST_LIST_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.LIST_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.LIST_ARRAY_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.LIST_RAW_GENERIC.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.LIST_MAP_STRING_INTEGER.getDeclaredField()));
        assertFalse(FormUrlUtils.isCollectionOfSimpleObj(listOf(new Object())));

        assertTrue(FormUrlUtils.isCollectionOfSimpleObj(listOf()));
        assertTrue(FormUrlUtils.isCollectionOfSimpleObj(listOf("1", "2")));
        assertTrue(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.LIST_INTEGER.getDeclaredField()));
        assertTrue(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.LIST_INTEGER.getGenericType()));
        assertTrue(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.LIST_STRING.getDeclaredField()));
        assertTrue(FormUrlUtils.isCollectionOfSimpleObj(PojoFields.LIST_STRING.getGenericType()));
    }

    @Test
    @DisplayName("isCollectionOfArray")
    public void test1647094280291() {
        assertFalse(FormUrlUtils.isCollectionOfArray(null));
        assertFalse(FormUrlUtils.isCollectionOfArray(new MapPojo()));
        assertFalse(FormUrlUtils.isCollectionOfArray(new GenericPojo<>()));
        assertFalse(FormUrlUtils.isCollectionOfArray(new Pojo[]{}));
        assertFalse(FormUrlUtils.isCollectionOfArray(PojoFields.INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfArray(PojoFields.NESTED_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfArray(PojoFields.MAP_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfArray(PojoFields.LIST_LIST_STRING.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfArray(PojoFields.LIST_LIST_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfArray(PojoFields.LIST_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfArray(PojoFields.LIST_RAW_GENERIC.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfArray(PojoFields.LIST_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfArray(PojoFields.LIST_STRING.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfArray(listOf(new Object())));
        assertFalse(FormUrlUtils.isCollectionOfArray(PojoFields.LIST_INTEGER.getDeclaredField()));
        assertFalse(FormUrlUtils.isCollectionOfArray(PojoFields.LIST_STRING.getDeclaredField()));
        assertFalse(FormUrlUtils.isCollectionOfArray(PojoFields.ARRAY_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfArray(PojoFields.LIST_MAP_STRING_INTEGER.getDeclaredField()));
        assertFalse(FormUrlUtils.isCollectionOfArray(arrayOf()));

        assertTrue(FormUrlUtils.isCollectionOfArray(listOf()));
        assertTrue(FormUrlUtils.isCollectionOfArray(listOf(arrayOf(1), arrayOf(2))));
        assertTrue(FormUrlUtils.isCollectionOfArray(PojoFields.LIST_ARRAY_INTEGER.getGenericType()));
        assertTrue(FormUrlUtils.isCollectionOfArray(PojoFields.LIST_ARRAY_INTEGER.getDeclaredField()));
    }

    @Test
    @DisplayName("isCollectionOfMap")
    public void test1647097043092() {
        assertFalse(FormUrlUtils.isCollectionOfMap(null));
        assertFalse(FormUrlUtils.isCollectionOfMap(new MapPojo()));
        assertFalse(FormUrlUtils.isCollectionOfMap(new GenericPojo<>()));
        assertFalse(FormUrlUtils.isCollectionOfMap(new Pojo[]{}));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.NESTED_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.MAP_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.LIST_LIST_STRING.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.LIST_LIST_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.LIST_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.LIST_RAW_GENERIC.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.LIST_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.LIST_STRING.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfMap(listOf(new Object())));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.LIST_INTEGER.getDeclaredField()));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.LIST_STRING.getDeclaredField()));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.ARRAY_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfMap(arrayOf()));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.LIST_ARRAY_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfMap(PojoFields.LIST_ARRAY_INTEGER.getDeclaredField()));

        assertTrue(FormUrlUtils.isCollectionOfMap(listOf()));
        assertTrue(FormUrlUtils.isCollectionOfMap(listOf(mapOf(), mapOf())));
        assertTrue(FormUrlUtils.isCollectionOfMap(PojoFields.LIST_MAP_STRING_INTEGER.getDeclaredField()));
    }

    @Test
    @DisplayName("isCollectionOfCollections")
    public void test1647097335935() {
        assertFalse(FormUrlUtils.isCollectionOfCollections(null));
        assertFalse(FormUrlUtils.isCollectionOfCollections(new MapPojo()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(new GenericPojo<>()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(new Pojo[]{}));
        assertFalse(FormUrlUtils.isCollectionOfCollections(PojoFields.INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(PojoFields.NESTED_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(PojoFields.MAP_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(PojoFields.LIST_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(PojoFields.LIST_RAW_GENERIC.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(PojoFields.LIST_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(PojoFields.LIST_STRING.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(listOf(new Object())));
        assertFalse(FormUrlUtils.isCollectionOfCollections(PojoFields.LIST_INTEGER.getDeclaredField()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(PojoFields.LIST_STRING.getDeclaredField()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(PojoFields.ARRAY_POJO.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(arrayOf()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(PojoFields.LIST_ARRAY_INTEGER.getGenericType()));
        assertFalse(FormUrlUtils.isCollectionOfCollections(PojoFields.LIST_ARRAY_INTEGER.getDeclaredField()));

        assertTrue(FormUrlUtils.isCollectionOfCollections(listOf()));
        assertTrue(FormUrlUtils.isCollectionOfCollections(listOf(listOf(), listOf())));
        assertTrue(FormUrlUtils.isCollectionOfCollections(PojoFields.LIST_LIST_INTEGER.getDeclaredField()));
        assertTrue(FormUrlUtils.isCollectionOfCollections(PojoFields.LIST_LIST_STRING.getGenericType()));
        assertTrue(FormUrlUtils.isCollectionOfCollections(PojoFields.LIST_LIST_POJO.getGenericType()));
    }

    @Test
    @DisplayName("isMapIChainList")
    public void test1647097187403() {
        assertFalse(FormUrlUtils.isMapIChainList(null));
        assertFalse(FormUrlUtils.isMapIChainList(chainListOf(true, 1, 2)));
        assertFalse(FormUrlUtils.isMapIChainList(chainListOf(true, listOf(1, 2), listOf(1, 2))));
        assertFalse(FormUrlUtils.isMapIChainList(chainListOf(true, arrayOf(1, 2), arrayOf(1, 2))));

        assertTrue(FormUrlUtils.isMapIChainList(chainListOf(true, mapOf(), mapOf())));
        assertTrue(FormUrlUtils.isMapIChainList(chainListOf(true)));
    }

}
