package org.touchbit.www.form.urlencoded.marshaller.chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.www.form.urlencoded.marshaller.BaseTest;
import org.touchbit.www.form.urlencoded.marshaller.util.ChainException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IChainPart.class unit tests")
public class IChainPartUnitTests extends BaseTest {

    @Nested
    @DisplayName("IChainPart.Default constructor tests")
    public class ChainPartConstructorTests {

        @Test
        @DisplayName("With value string")
        public void test1646348664848() {
            final IChainPart.Default chainPart = new IChainPart.Default("foo", "bar", true, false);
            assertTrue(chainPart.isImplicitList());
            assertFalse(chainPart.isExplicitList());
            assertIs(chainPart.getKey(), "foo");
            assertIs(chainPart.getValue(), "bar");
            assertIs(chainPart.toString(), "foo=bar");
        }

        @Test
        @DisplayName("Without value string")
        public void test1646348994175() {
            final IChainPart.Default chainPart = new IChainPart.Default("foo", false, true);
            assertFalse(chainPart.isImplicitList());
            assertTrue(chainPart.isExplicitList());
            assertIs(chainPart.getKey(), "foo");
            assertIsNull(chainPart.getValue());
            assertIs(chainPart.toString(), "foo=<null>");
        }
    }

    @Nested
    @DisplayName("#setValue() method tests")
    public class SetValueMethodTests {

        @Test
        @DisplayName("Allow null value")
        public void test1646349128628() {
            final IChainPart.Default chainPart = new IChainPart.Default("foo", "bar", true, true);
            assertIs(chainPart.getValue(), "bar");
            chainPart.setValue(null);
            assertIsNull(chainPart.getValue());
        }

        @Test
        @DisplayName("Allow string value")
        public void test1646349254923() {
            final IChainPart.Default chainPart = new IChainPart.Default("foo", null, true, true);
            assertIsNull(chainPart.getValue());
            chainPart.setValue("bar");
            assertIs(chainPart.getValue(), "bar");
        }
    }

    @Nested
    @DisplayName("#copy() method tests")
    public class CopyMethodTests {

        @Test
        @DisplayName("Objects is same")
        public void test1646349369640() {
            final IChainPart.Default part = new IChainPart.Default("foo", "bar", true, true);
            final IChainPart.Default copy = part.copy();
            assertNotEquals(part, copy);
            assertIs(part.isImplicitList(), copy.isImplicitList());
            assertIs(part.isExplicitList(), copy.isExplicitList());
            assertIs(part.getKey(), copy.getKey());
            assertIs(part.getValue(), copy.getValue());
            assertIs(part.toString(), copy.toString());
        }
    }

    @Nested
    @DisplayName("#addIndex() method tests")
    public class AddIndexMethodTests {

        @Test
        @DisplayName("Add implicit index if implicitList=true && explicitList=false")
        public void test1646349560734() {
            final IChainPart.Default part = new IChainPart.Default("foo", "bar", true, false);
            part.appendIndex(0);
            assertIs(part.getKey(), "foo[]");
        }

        @Test
        @DisplayName("Add explicit index if implicitList=false && explicitList=true")
        public void test1646349691126() {
            final IChainPart.Default part = new IChainPart.Default("foo", "bar", false, true);
            part.appendIndex(0);
            assertIs(part.getKey(), "foo[0]");
        }

        @Test
        @DisplayName("Add explicit index if implicitList=true && explicitList=true")
        public void test1646349980152() {
            final IChainPart.Default part = new IChainPart.Default("foo", "bar", true, true);
            part.appendIndex(0);
            assertIs(part.getKey(), "foo[0]");
        }

        @Test
        @DisplayName("Add hidden index if implicitList=false && explicitList=false")
        public void test1646350018806() {
            final IChainPart.Default part = new IChainPart.Default("foo", "bar", false, false);
            part.appendIndex(0);
            assertIs(part.getKey(), "foo");
        }

        @Test
        @DisplayName("ChainException if index is negative")
        public void test1646397732588() {
            assertThrow(() -> new IChainPart.Default("foo1", null, false, false).appendIndex(-1))
                    .assertClass(ChainException.class)
                    .assertMessageIs("Array index cannot be negative but got -1");
        }

    }

    @Nested
    @DisplayName("#addPart() method tests")
    public class AddPartMethodTests {

        @Test
        @DisplayName("Add key part if value not present")
        public void test1646350096901() {
            final IChainPart.Default part = new IChainPart.Default("foo1", false, false);
            part.appendPart("foo2");
            assertIs(part.getKey(), "foo1[foo2]");
        }

        @Test
        @DisplayName("ChainException when adding a key part if value present")
        public void test1646350171867() {
            assertThrow(() -> new IChainPart.Default("foo1", "bar", false, false).appendPart("foo2"))
                    .assertClass(ChainException.class)
                    .assertMessageIs("It is forbidden to change the key if the value is already set.\n" +
                                     "Key: foo1\n" +
                                     "Val: bar\n" +
                                     "Wrong part: foo2\n");
        }
    }

    @Nested
    @DisplayName("#getKeyChain() method tests")
    public class GetChainKeyChainMethodTests {

        @Test
        @DisplayName("Get simple keychain")
        public void test1646350344883() {
            final IChainPart.Default part = new IChainPart.Default("foo", false, false);
            final List<IChainKey> keyChain = part.getKeyChain();
            assertThat(keyChain).hasSize(1);
            assertIs(keyChain.get(0).getKeyName(), "foo");
        }

        @Test
        @DisplayName("Get complex keychain")
        public void test1646399604034() {
            final IChainPart.Default part = new IChainPart.Default("foo", false, false);
            part.appendPart("bar");
            final List<IChainKey> keyChain = part.getKeyChain();
            assertThat(keyChain).hasSize(2);
            assertIs(keyChain.get(0).getKeyName(), "foo");
            assertIs(keyChain.get(1).getKeyName(), "bar");
        }

        @Test
        @DisplayName("Get implicit indexed keychain")
        public void test1646399728137() {
            final IChainPart.Default part = new IChainPart.Default("foo", true, false);
            part.appendIndex(0);
            final List<IChainKey> keyChain = part.getKeyChain();
            assertThat(keyChain).hasSize(2);
            assertIs(keyChain.get(0).getKeyName(), "foo");
            assertIsNull(keyChain.get(1).getKeyName());
            assertIsNull(keyChain.get(1).getIndex());
        }

        @Test
        @DisplayName("Get explicit indexed keychain")
        public void test1646399830902() {
            final IChainPart.Default part = new IChainPart.Default("foo", false, true);
            part.appendIndex(0);
            final List<IChainKey> keyChain = part.getKeyChain();
            assertThat(keyChain).hasSize(2);
            assertIs(keyChain.get(0).getKeyName(), "foo");
            assertIsNull(keyChain.get(1).getKeyName());
            assertIs(keyChain.get(1).getIndex(), 0);
        }

        @Test
        @DisplayName("Get simple keychain if key is empty")
        public void test1646400035703() {
            final IChainPart.Default part = new IChainPart.Default("", false, true);
            final List<IChainKey> keyChain = part.getKeyChain();
            assertThat(keyChain).hasSize(1);
            assertIsNull(keyChain.get(0).getKeyName());
            assertIsNull(keyChain.get(0).getIndex());
        }

    }

    @SuppressWarnings("unchecked")
    @Nested
    @DisplayName("#getRawDataValue() method tests")
    public class GetRawDataValueMethodTests {

        @Test
        @DisplayName("Build difficult raw data from key with nested structures (indexed array)")
        public void test1646400741721() {
            final IChainPart.Default part = new IChainPart.Default("foo", false, true)
                    .appendPart("bar")
                    .appendIndex(0)
                    .appendPart("rob")
                    .appendIndex(1);
            final Map<String, Object> rawDataValue = part.getRawDataValue();
            assertThat(rawDataValue).isNotEmpty();
            assertThat(rawDataValue.get("foo")).isNotNull();
            assertThat(rawDataValue.get("foo")).isInstanceOf(Map.class);
            final Map<String, Object> fooMap = (Map<String, Object>) rawDataValue.get("foo");
            assertThat(fooMap.get("bar")).isNotNull();
            assertThat(fooMap.get("bar")).isInstanceOf(List.class);
            final List<Map<String, Object>> bar = (List<Map<String, Object>>) fooMap.get("bar");
            assertThat(bar).hasSize(1);
            assertThat(bar.get(0)).isInstanceOf(Map.class);
            assertThat(bar.get(0)).isNotEmpty();
            final Map<String, Object> barMap = bar.get(0);
            assertThat(barMap).isNotEmpty();
            assertThat(barMap.get("rob")).isNotNull();
            assertThat(barMap.get("rob")).isInstanceOf(List.class);
            final List<Object> robList = (List<Object>) barMap.get("rob");
            assertThat(robList).hasSize(2);
            assertThat(robList).contains(null, (Object) null);
        }

        @Test
        @DisplayName("Build difficult raw data from key with nested structures (unindexed array)")
        public void test1646402894472() {
            final IChainPart.Default part = new IChainPart.Default("foo", true, false)
                    .appendPart("bar")
                    .appendIndex(0)
                    .appendPart("rob")
                    .appendIndex(1);
            final Map<String, Object> rawDataValue = part.getRawDataValue();
            assertThat(rawDataValue).isNotEmpty();
            assertThat(rawDataValue.get("foo")).isNotNull();
            assertThat(rawDataValue.get("foo")).isInstanceOf(Map.class);
            final Map<String, Object> fooMap = (Map<String, Object>) rawDataValue.get("foo");
            assertThat(fooMap.get("bar")).isNotNull();
            assertThat(fooMap.get("bar")).isInstanceOf(List.class);
            final List<Map<String, Object>> bar = (List<Map<String, Object>>) fooMap.get("bar");
            assertThat(bar).hasSize(1);
            assertThat(bar.get(0)).isInstanceOf(Map.class);
            assertThat(bar.get(0)).isNotEmpty();
            final Map<String, Object> barMap = bar.get(0);
            assertThat(barMap).isNotEmpty();
            assertThat(barMap.get("rob")).isNotNull();
            assertThat(barMap.get("rob")).isInstanceOf(List.class);
            final List<Object> robList = (List<Object>) barMap.get("rob");
            assertThat(robList).hasSize(1);
            assertThat(robList).contains((Object) null);
        }

        @Test
        @DisplayName("Build difficult raw data from key with nested structures (without array)")
        public void test1646402977647() {
            final IChainPart.Default part = new IChainPart.Default("foo", false, false)
                    .appendPart("bar")
                    .appendIndex(0)
                    .appendPart("rob")
                    .appendIndex(1);
            final Map<String, Object> rawDataValue = part.getRawDataValue();
            assertThat(rawDataValue).isNotEmpty();
            assertThat(rawDataValue.get("foo")).isNotNull();
            assertThat(rawDataValue.get("foo")).isInstanceOf(Map.class);
            final Map<String, Object> fooMap = (Map<String, Object>) rawDataValue.get("foo");
            assertThat(fooMap.get("bar")).isNotNull();
            assertThat(fooMap.get("bar")).isInstanceOf(Map.class);
            final Map<String, Object> barMap = (Map<String, Object>) fooMap.get("bar");
            assertThat(barMap).isNotEmpty();
            assertThat(barMap.get("rob")).isNull();
        }

        @Test
        @DisplayName("ChainException if firs key is array index")
        public void test1646404144957() {
            assertThrow(() -> new IChainPart.Default("0", true, true).getRawDataValue())
                    .assertClass(ChainException.class)
                    .assertMessageIs("Unable to process key. The key does not belong to the 'Map' type.\n" +
                                     "Key: 0\n" +
                                     "Key type: " + IChainList.Default.class + "\n" +
                                     "Key structure: [null]\n");
        }

    }

}
