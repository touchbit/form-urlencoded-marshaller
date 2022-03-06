package org.touchbit.www.form.url.codec.chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.www.form.url.BaseTest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IChain.class unit tests")
public class IChainUnitTests extends BaseTest {

    @Nested
    @DisplayName("IChain.Default constructor tests")
    public class ConstructorMethodTests {

        @Test
        @DisplayName("Build from url encoded string")
        public void test1646498055251() {
            final IChain.Default aDefault = new IChain.Default("foo=%20");
            assertNotNull(aDefault.getRawData());
            assertIs(aDefault.getRawData().get("foo"), "%20");
            assertThat(aDefault.getChainParts()).isNotEmpty();
            assertIs(aDefault.getChainParts().get(0).toString(), "foo=%20");
            assertIs(aDefault.toString(), "foo=%20");
        }

        @Test
        @DisplayName("Build from url encoded string (null)")
        public void test1646498193999() {
            final IChain.Default aDefault = new IChain.Default(null);
            assertThat(aDefault.getRawData()).isEmpty();
            assertThat(aDefault.getChainParts()).isEmpty();
        }

        @Test
        @DisplayName("Build from url encoded string (empty string)")
        public void test1646498257354() {
            final IChain.Default aDefault = new IChain.Default("");
            assertNotNull(aDefault.getRawData());
            assertThat(aDefault.getRawData()).isEmpty();
            assertThat(aDefault.getChainParts()).isEmpty();
        }

        @Test
        @DisplayName("Build from url encoded string (blank string)")
        public void test1646498371061() {
            final IChain.Default aDefault = new IChain.Default("      \n    ");
            assertNotNull(aDefault.getRawData());
            assertThat(aDefault.getRawData()).isEmpty();
            assertThat(aDefault.getChainParts()).isEmpty();
        }

        @Test
        @DisplayName("Build from raw data map")
        public void test1646498411584() {
            final Map<String, Object> rawData = new HashMap<>();
            rawData.put("bar", "car");
            rawData.put("foo", "%20");
            final IChain.Default aDefault = new IChain.Default(rawData, true, true);
            assertNotNull(aDefault.getRawData());
            assertIs(aDefault.getRawData().get("foo"), "%20");
            assertThat(aDefault.getChainParts()).isNotEmpty();
            assertIs(aDefault.getChainParts().get(0).toString(), "bar=car");
            assertIs(aDefault.toString(), "bar=car&foo=%20");
            assertIs(aDefault.toString(true), "bar=car&\nfoo=%20");
        }

        @Test
        @DisplayName("Build from raw data map (empty map)")
        public void test1646498629197() {
            final Map<String, Object> rawData = new HashMap<>();
            final IChain.Default aDefault = new IChain.Default(rawData, true, true);
            assertNotNull(aDefault.getRawData());
            assertThat(aDefault.getRawData()).isEmpty();
            assertThat(aDefault.getChainParts()).isEmpty();
        }

    }

    @Nested
    @DisplayName("#collectionToChainPart() method tests")
    public class CollectionToChainPartMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646512626465() {
            final IChain.Default chain = new IChain.Default("foo=%20");
            final IChainPart.Default bar = new IChainPart.Default("bar", false, false);
            assertNPE(() -> chain.collectionToChainPart(null, new ArrayList<>()), "chainPart");
            assertNPE(() -> chain.collectionToChainPart(bar, null), "value");
        }

        @Test
        @DisplayName("Transform List to ChainParts (explicit array)")
        public void test1646512776111() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            final List<Object> list = new ArrayList<>();
            list.add("foo");
            list.add("bar");
            final List<IChainPart> parts = chain.collectionToChainPart(bar, list);
            assertThat(parts).hasSize(2);
            assertIs(parts.get(0).toString(), "test[0]=foo");
            assertIs(parts.get(1).toString(), "test[1]=bar");
        }

        @Test
        @DisplayName("Transform List to ChainParts (implicit array)")
        public void test1646513073341() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, false);
            final List<Object> list = new ArrayList<>();
            list.add("foo");
            list.add("bar");
            final List<IChainPart> parts = chain.collectionToChainPart(bar, list);
            assertThat(parts).hasSize(2);
            assertIs(parts.get(0).toString(), "test[]=foo");
            assertIs(parts.get(1).toString(), "test[]=bar");
        }

        @Test
        @DisplayName("Transform List to ChainParts (hidden array)")
        public void test1646513100444() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", false, false);
            final List<Object> list = new ArrayList<>();
            list.add("foo");
            list.add("bar");
            list.add(null);
            final List<IChainPart> parts = chain.collectionToChainPart(bar, list);
            assertThat(parts).hasSize(2);
            assertIs(parts.get(0).toString(), "test=foo");
            assertIs(parts.get(1).toString(), "test=bar");
        }

        @Test
        @DisplayName("Transform Set to ChainParts (explicit array)")
        public void test1646513145969() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            final Set<Object> set = new HashSet<>();
            set.add("foo");
            set.add("bar");
            final List<IChainPart> parts = chain.collectionToChainPart(bar, set);
            assertThat(parts).hasSize(2);
            assertIs(parts.get(0).toString(), "test[0]=bar");
            assertIs(parts.get(1).toString(), "test[1]=foo");
        }

    }

    @Nested
    @DisplayName("#simpleToChainPart() method tests")
    public class SimpleToChainPartMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646525049194() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            assertNPE(() -> chain.simpleToChainPart(null, new Object()), "chainPart");
            assertNPE(() -> chain.simpleToChainPart(bar, null), "value");
        }

        @Test
        @DisplayName("Applied obj.toString()")
        public void test1646525136730() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            final IChainPart result = chain.simpleToChainPart(bar, new HashMap<>());
            assertIs(result.toString(), "test={}");
        }
    }

    @Nested
    @DisplayName("#mapToChainPart() method tests")
    public class MapToChainPartMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646525291475() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            assertNPE(() -> chain.mapToChainPart(null, new HashMap<>()), "chainPart");
            assertNPE(() -> chain.mapToChainPart(bar, null), "value");
        }

        @Test
        @DisplayName("return empty list if map is empty")
        public void test1646525479939() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            final List<IChainPart> parts = chain.mapToChainPart(bar, new HashMap<>());
            assertThat(parts).isEmpty();
        }

        @Test
        @DisplayName("return not empty list if map has sting value")
        public void test1646525550280() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            final HashMap<Object, Object> map = new HashMap<>();
            map.put("bar", Collections.singleton("1"));
            map.put("foo", Collections.singleton("2"));
            final List<IChainPart> parts = chain.mapToChainPart(bar, map);
            assertThat(parts).hasSize(2);
            assertIs(parts.get(0).toString(), "test[bar][0]=1");
            assertIs(parts.get(1).toString(), "test[foo][0]=2");
        }

        @Test
        @DisplayName("return not empty list if map has list value")
        public void test1646525744798() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            final HashMap<Object, Object> map = new HashMap<>();
            map.put("bar", "1");
            map.put("foo", "2");
            final List<IChainPart> parts = chain.mapToChainPart(bar, map);
            assertThat(parts).hasSize(2);
            assertIs(parts.get(0).toString(), "test[bar]=1");
            assertIs(parts.get(1).toString(), "test[foo]=2");
        }


    }

    @Nested
    @DisplayName("#valueObjectToChainParts() method tests")
    public class ValueObjectToChainPartsMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646583838933() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            assertNPE(() -> chain.valueObjectToChainParts(null, new HashMap<>()), "chainPart");
            assertNPE(() -> chain.valueObjectToChainParts(bar, null), "value");
        }

        @Test
        @DisplayName("Convert simple type to ChainPart list")
        public void test1646583894688() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            final List<IChainPart> parts = chain.valueObjectToChainParts(bar, 1);
            assertThat(parts).hasSize(1);
            assertIs(parts.get(0).toString(), "test=1");
        }

        @Test
        @DisplayName("Convert List to ChainPart list")
        public void test1646583985419() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            final List<IChainPart> parts = chain.valueObjectToChainParts(bar, listOf(1, 2));
            assertThat(parts).hasSize(2);
            assertIs(parts.get(0).toString(), "test[0]=1");
            assertIs(parts.get(1).toString(), "test[1]=2");
        }

        @Test
        @DisplayName("Convert Set to ChainPart list")
        public void test1646584093909() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            final List<IChainPart> parts = chain.valueObjectToChainParts(bar, setOf(1, 2));
            assertThat(parts).hasSize(2);
            assertIs(parts.get(0).toString(), "test[0]=1");
            assertIs(parts.get(1).toString(), "test[1]=2");
        }

        @Test
        @DisplayName("Convert Map to ChainPart list")
        public void test1646584131434() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            final List<IChainPart> parts = chain.valueObjectToChainParts(bar, mapOf("foo", "bar"));
            assertThat(parts).hasSize(1);
            assertIs(parts.get(0).toString(), "test[foo]=bar");
        }

        @Test
        @DisplayName("IllegalArgumentException - unsupported value type")
        public void test1646584333520() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            assertThrow(() -> chain.valueObjectToChainParts(bar, mapOf("foo", new Object())))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("Unsupported value type: java.lang.Object");
        }
    }

    @Nested
    @DisplayName("#readModel() method tests")
    public class ReadModelMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646584579737() {
            final IChain.Default chain = new IChain.Default(null);
            assertNPE(() -> chain.readModel(null, true, true), "rawData");
        }

        @Test
        @DisplayName("Return empty list if raw data map is empty")
        public void test1646584611024() {
            final IChain.Default chain = new IChain.Default(null);
            final List<IChainPart> parts = chain.readModel(new HashMap<>(), true, true);
            assertThat(parts).isEmpty();
        }

        @Test
        @DisplayName("Return non-empty list if raw data map is not empty")
        public void test1646588665516() {
            final IChain.Default chain = new IChain.Default(null);
            final List<IChainPart> parts = chain.readModel(mapOf("foo", "bar"), true, true);
            assertThat(parts).hasSize(1);
            assertThat(parts.get(0).toString()).isEqualTo("foo=bar");
        }
    }

    @Nested
    @DisplayName("#isEvenBracketsRatio() method tests")
    public class IsEvenBracketsRatioMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646581863035() {
            final IChain.Default chain = new IChain.Default(null);
            assertNPE(() -> chain.isEvenBracketsRatio(null), "key");
        }

        @Test
        @DisplayName("return true if the list key is sequential (indexed)")
        public void test1646581898521() {
            final IChain.Default chain = new IChain.Default(null);
            assertTrue(chain.isEvenBracketsRatio("key[0][1][2]"));
        }

        @Test
        @DisplayName("return true if the list key is sequential (unindexed)")
        public void test1646581975124() {
            final IChain.Default chain = new IChain.Default(null);
            assertTrue(chain.isEvenBracketsRatio("key[][][]"));
        }

        @Test
        @DisplayName("return true if the list key is sequential (nested)")
        public void test1646581988424() {
            final IChain.Default chain = new IChain.Default(null);
            assertTrue(chain.isEvenBracketsRatio("key[[[]]]"));
        }

        @Test
        @DisplayName("return true if the list not present")
        public void test1646582010654() {
            final IChain.Default chain = new IChain.Default(null);
            assertTrue(chain.isEvenBracketsRatio("key"));
        }

        @Test
        @DisplayName("return false if key contains odd ratio of brackets")
        public void test1646582039541() {
            final IChain.Default chain = new IChain.Default(null);
            assertFalse(chain.isEvenBracketsRatio("key[[]"));
        }
    }

    @Nested
    @DisplayName("#hasNestedBrackets() method tests")
    public class HasNestedBracketsMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646590105290() {
            final IChain.Default chain = new IChain.Default(null);
            assertNPE(() -> chain.hasNestedBrackets(null), "key");
        }

        @Test
        @DisplayName("return true if key contains [[")
        public void test1646590121050() {
            final IChain.Default chain = new IChain.Default(null);
            assertTrue(chain.hasNestedBrackets("[["));
        }

        @Test
        @DisplayName("return true if key contains ]]")
        public void test1646590199295() {
            final IChain.Default chain = new IChain.Default(null);
            assertTrue(chain.hasNestedBrackets("]]"));
        }

        @Test
        @DisplayName("return false if key=[][]")
        public void test1646590220520() {
            final IChain.Default chain = new IChain.Default(null);
            assertFalse(chain.hasNestedBrackets("[][]"));
        }
    }

    @Nested
    @DisplayName("#assertKeyBrackets() method tests")
    public class AssertKeyBracketsMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646590274538() {
            final IChain.Default chain = new IChain.Default(null);
            assertNPE(() -> chain.assertKeyBrackets(null), "key");
        }

        @Test
        @DisplayName("Successfully key asserting if key in URL form format")
        public void test1646590527993() {
            final IChain.Default chain = new IChain.Default(null);
            chain.assertKeyBrackets("foo[bar][0][1][car]=value");
        }

        @Test
        @DisplayName("IllegalArgumentException incorrect ratio of opening and closing brackets")
        public void test1646590330285() {
            final IChain.Default chain = new IChain.Default(null);
            assertThrow(() -> chain.assertKeyBrackets("foo[bar]0]=value"))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("The key contains an incorrect ratio of opening and closing brackets.\n" +
                                     "Invalid key: foo[bar]0]=value\n");
        }

        @Test
        @DisplayName("IllegalArgumentException list nesting [[]]")
        public void test1646590381283() {
            final IChain.Default chain = new IChain.Default(null);
            assertThrow(() -> chain.assertKeyBrackets("foo[bar][[0]]=value"))
                    .assertClass(IllegalArgumentException.class)
                    .assertMessageIs("Key nesting is not allowed.\n" +
                                     "Invalid key: foo[bar][[0]]=value\n" +
                                     "Expected nested object format: filter[foo][bar]\n" +
                                     "Expected nested list format: filter[foo][0]\n");
        }
    }

    @Nested
    @DisplayName("#readUrlEncodedString() method tests")
    public class ReadUrlEncodedStringMethodTests {


    }

}
