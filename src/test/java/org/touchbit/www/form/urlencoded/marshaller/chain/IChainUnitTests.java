package org.touchbit.www.form.urlencoded.marshaller.chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.www.form.urlencoded.marshaller.BaseTest;
import org.touchbit.www.form.urlencoded.marshaller.util.ChainException;

import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
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
            assertIs(aDefault.getRawData().get("foo"), " ");
            assertThat(aDefault.getChainParts()).isNotEmpty();
            assertIs(aDefault.getChainParts().get(0).toString(), "foo= ");
            assertIs(aDefault.toString(), "foo= ");
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
            assertRequired(() -> chain.collectionToChainPart(null, new ArrayList<>()), "chainPart");
            assertRequired(() -> chain.collectionToChainPart(bar, null), "value");
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
            assertRequired(() -> chain.simpleToChainPart(null, new Object()), "chainPart");
            assertRequired(() -> chain.simpleToChainPart(bar, null), "value");
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
            assertRequired(() -> chain.mapToChainPart(null, new HashMap<>()), "chainPart");
            assertRequired(() -> chain.mapToChainPart(bar, null), "value");
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
            assertRequired(() -> chain.valueObjectToChainParts(null, new HashMap<>()), "chainPart");
            assertRequired(() -> chain.valueObjectToChainParts(bar, null), "value");
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
        @DisplayName("ChainException - unsupported value type")
        public void test1646584333520() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainPart.Default bar = new IChainPart.Default("test", true, true);
            assertThrow(() -> chain.valueObjectToChainParts(bar, mapOf("foo", UTF_8)))
                    .assertClass(ChainException.class)
                    .assertMessageIs("\n  Unsupported type for conversion.\n" +
                                     "    Actual type: sun.nio.cs.UTF_8\n" +
                                     "    Actual value: UTF-8\n" +
                                     "    Expected: simple reference types (String, Integer, Boolean, etc.)\n" +
                                     "    Expected: heirs of java.util.Map\n" +
                                     "    Expected: heirs of java.util.Collection\n");
        }
    }

    @Nested
    @DisplayName("#readModel() method tests")
    public class ReadModelMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646584579737() {
            final IChain.Default chain = new IChain.Default(null);
            assertRequired(() -> chain.readModel(null, true, true), "rawData");
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
            assertRequired(() -> chain.isEvenBracketsRatio(null), "key");
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
            assertRequired(() -> chain.hasNestedBrackets(null), "key");
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
            assertRequired(() -> chain.assertKeyBrackets(null), "key");
        }

        @Test
        @DisplayName("Successfully key asserting if key in URL form format")
        public void test1646590527993() {
            final IChain.Default chain = new IChain.Default(null);
            chain.assertKeyBrackets("foo[bar][0][1][car]=value");
        }

        @Test
        @DisplayName("ChainException incorrect ratio of opening and closing brackets")
        public void test1646590330285() {
            final IChain.Default chain = new IChain.Default(null);
            assertThrow(() -> chain.assertKeyBrackets("foo[bar]0]=value"))
                    .assertClass(ChainException.class)
                    .assertMessageIs("The key contains an incorrect ratio of opening and closing brackets.\n" +
                                     "Invalid key: foo[bar]0]=value\n");
        }

        @Test
        @DisplayName("ChainException list nesting [[]]")
        public void test1646590381283() {
            final IChain.Default chain = new IChain.Default(null);
            assertThrow(() -> chain.assertKeyBrackets("foo[bar][[0]]=value"))
                    .assertClass(ChainException.class)
                    .assertMessageIs("Key nesting is not allowed.\n" +
                                     "Invalid key: foo[bar][[0]]=value\n" +
                                     "Expected nested object format: filter[foo][bar]\n" +
                                     "Expected nested list format: filter[foo][0]\n");
        }
    }

    @Nested
    @DisplayName("#readUrlEncodedString() method tests")
    public class ReadUrlEncodedStringMethodTests {

        @Test
        @DisplayName("return empty list if urlEncodedString = null")
        public void test1646591957105() {
            final IChain.Default chain = new IChain.Default(null);
            final List<IChainPart> parts = chain.readUrlEncodedString(null, UTF_8);
            assertThat(parts).isEmpty();
        }

        @Test
        @DisplayName("return empty list if urlEncodedString = ''")
        public void test1646592015888() {
            final IChain.Default chain = new IChain.Default(null);
            final List<IChainPart> parts = chain.readUrlEncodedString("", UTF_8);
            assertThat(parts).isEmpty();
        }

        @Test
        @DisplayName("return empty list if urlEncodedString = '   \n    '")
        public void test1646592033216() {
            final IChain.Default chain = new IChain.Default(null);
            final List<IChainPart> parts = chain.readUrlEncodedString("   \n    ", UTF_8);
            assertThat(parts).isEmpty();
        }

        @Test
        @DisplayName("return IChainPart list if urlEncodedString = 'foo[0]=val1&bar[1]='")
        public void test1646592054392() {
            final IChain.Default chain = new IChain.Default(null);
            final List<IChainPart> parts = chain.readUrlEncodedString("foo[0]=val1&bar[1]=", UTF_8);
            assertThat(parts).hasSize(2);
            assertThat(parts.get(0).toString()).isEqualTo("foo[0]=val1");
            assertThat(parts.get(1).toString()).isEqualTo("bar[1]=");
        }

        @Test
        @DisplayName("ChainException key-value pair is not in URL form format (foo)")
        public void test1646592184246() {
            final IChain.Default chain = new IChain.Default(null);
            assertThrow(() -> chain.readUrlEncodedString("foo", UTF_8))
                    .assertClass(ChainException.class)
                    .assertMessageIs("URL encoded key-value pair is not in URL format:\n" +
                                     "Pair: foo");
        }

        @Test
        @DisplayName("ChainException key-value pair is not in URL form format (foo=bar=val)")
        public void test1646592215480() {
            final IChain.Default chain = new IChain.Default(null);
            assertThrow(() -> chain.readUrlEncodedString("foo=bar=val", UTF_8))
                    .assertClass(ChainException.class)
                    .assertMessageIs("URL encoded key-value pair is not in URL format:\n" +
                                     "Pair: foo=bar=val");
        }
    }

    @Nested
    @DisplayName("#mergeNonIndexedIChainLists() method tests")
    public class MergeNonIndexedIChainListsMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646663516549() {
            final IChain.Default chain = new IChain.Default(null);
            assertRequired(() -> chain.mergeNonIndexedIChainLists(null, new IChainList.Default(true)), "source");
            assertRequired(() -> chain.mergeNonIndexedIChainLists(new IChainList.Default(true), null), "target");
        }

        @Test
        @DisplayName("append source list to target list (unindexed)")
        public void test1646593062091() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(false);
            target.add(null);
            target.add("foo");
            final IChainList.Default source = new IChainList.Default(false);
            source.add("bar");
            final IChainList objects = chain.mergeNonIndexedIChainLists(source, target);
            assertThat(objects.toString()).isEqualTo("[null, foo, bar]");
        }

        @Test
        @DisplayName("append source maps list to target maps list if maps keys is same (unindexed)")
        public void test1646654789221() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(false);
            target.add(mapOf("foo", "target_value"));
            final IChainList.Default source = new IChainList.Default(false);
            source.add(mapOf("foo", "source_value"));
            final IChainList objects = chain.mergeNonIndexedIChainLists(source, target);
            assertThat(objects.toString()).isEqualTo("[{foo=target_value}, {foo=source_value}]");
        }

        @Test
        @DisplayName("straight lists if target list more than source list (unindexed)")
        public void test1646593247852() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(false);
            target.add("foo");
            final IChainList.Default source = new IChainList.Default(false);
            source.add(null);
            source.add("bar");
            final IChainList objects = chain.mergeNonIndexedIChainLists(source, target);
            assertThat(objects).hasSize(3);
            assertThat(objects).containsExactly("foo", null, "bar");
        }

        @Test
        @DisplayName("merge nullable lists (unindexed list)")
        public void test1646594612101() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(false);
            target.add(null);
            final IChainList.Default source = new IChainList.Default(false);
            source.add(null);
            source.add(null);
            final IChainList objects = chain.mergeNonIndexedIChainLists(source, target);
            assertThat(objects).hasSize(3);
            assertThat(objects).containsExactly(null, null, null);
        }

        @Test
        @DisplayName("merge empty target and source list (unindexed)")
        public void test1646656478924() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(false);
            final IChainList.Default source = new IChainList.Default(false);
            final IChainList objects = chain.mergeNonIndexedIChainLists(source, target);
            assertThat(objects).isEqualTo(target);
        }

        @Test
        @DisplayName("merge internal map (unindexed list)")
        public void test1646594061126() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(false);
            target.add(mapOf("foo", "a"));
            final IChainList.Default source = new IChainList.Default(false);
            source.add(mapOf("bar", "b"));
            final IChainList objects = chain.mergeNonIndexedIChainLists(source, target);
            assertThat(objects).hasSize(1);
            assertThat(objects).containsExactly(mapOf("foo", "a", "bar", "b"));
        }

    }

    @Nested
    @DisplayName("#mergeIndexedIChainLists() method tests")
    public class MergeIndexedIChainListsMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646663522227() {
            final IChain.Default chain = new IChain.Default(null);
            assertRequired(() -> chain.mergeIndexedIChainLists(null, new IChainList.Default(true)), "source");
            assertRequired(() -> chain.mergeIndexedIChainLists(new IChainList.Default(true), null), "target");
        }

        @Test
        @DisplayName("reverse lists if target list more than source list (indexed)")
        public void test1646592893081() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            target.add(null);
            target.add("foo");
            final IChainList.Default source = new IChainList.Default(true);
            source.add("bar");
            final IChainList objects = chain.mergeIndexedIChainLists(source, target);
            assertThat(objects.toString()).isEqualTo("[bar, foo]");
        }

        @Test
        @DisplayName("merge lists if target list length equals source list length (indexed)")
        public void test1646655610531() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            target.add(null);
            target.add("foo");
            final IChainList.Default source = new IChainList.Default(true);
            source.add("bar");
            source.add(null);
            final IChainList objects = chain.mergeIndexedIChainLists(source, target);
            assertThat(objects.toString()).isEqualTo("[bar, foo]");
        }

        @Test
        @DisplayName("straight lists if target list more than source list (indexed)")
        public void test1646593238145() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            target.add("foo");
            final IChainList.Default source = new IChainList.Default(true);
            source.add(null);
            source.add("bar");
            final IChainList objects = chain.mergeIndexedIChainLists(source, target);
            assertThat(objects.toString()).isEqualTo("[foo, bar]");
        }

        @Test
        @DisplayName("insert internal map (indexed list)")
        public void test1646593443676() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            target.add(mapOf("foo", "a"));
            final IChainList.Default source = new IChainList.Default(true);
            source.add(null);
            source.add(mapOf("bar", "b"));
            final IChainList objects = chain.mergeIndexedIChainLists(source, target);
            assertThat(objects).hasSize(2);
            assertThat(objects).containsExactly(mapOf("foo", "a"), mapOf("bar", "b"));
        }

        @Test
        @DisplayName("merge internal map if source not filled (indexed list)")
        public void test1646593706927() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            target.add(mapOf("bar", "b"));
            target.add(mapOf("foo1", "a"));
            final IChainList.Default source = new IChainList.Default(true);
            source.add(null);
            source.add(mapOf("foo2", "c"));
            final IChainList objects = chain.mergeIndexedIChainLists(source, target);
            assertThat(objects).hasSize(2);
            assertThat(objects).containsExactly(mapOf("bar", "b"), mapOf("foo1", "a", "foo2", "c"));
        }

        @Test
        @DisplayName("merge internal map if source is filled (indexed list)")
        public void test1646593979381() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            target.add(mapOf("bar", "b"));
            final IChainList.Default source = new IChainList.Default(true);
            source.add(mapOf("foo2", "c"));
            final IChainList objects = chain.mergeIndexedIChainLists(source, target);
            assertThat(objects).hasSize(1);
            assertThat(objects).containsExactly(mapOf("bar", "b", "foo2", "c"));
        }

        @Test
        @DisplayName("merge nullable lists (indexed list)")
        public void test1646594509714() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            target.add(null);
            final IChainList.Default source = new IChainList.Default(true);
            source.add(null);
            source.add(null);
            final IChainList objects = chain.mergeIndexedIChainLists(source, target);
            assertThat(objects).hasSize(2);
            assertThat(objects).containsExactly(null, null);
        }

        @Test
        @DisplayName("merge empty target and source list (indexed)")
        public void test1646594736999() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            final IChainList.Default source = new IChainList.Default(true);
            final IChainList objects = chain.mergeIndexedIChainLists(source, target);
            assertThat(objects).isEqualTo(target);
        }

        @Test
        @DisplayName("overwrite value by same index (indexed)")
        public void test1646656532750() {
            // foo[0]=bar&foo[0]=car -> foo=[car]
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            target.add("bar");
            final IChainList.Default source = new IChainList.Default(true);
            source.add("car");
            final IChainList objects = chain.mergeIndexedIChainLists(source, target);
            assertThat(objects.toString()).isEqualTo("[car]");
        }

    }

    @Nested
    @DisplayName("#mergeIChainLists() method tests")
    public class MergeIChainListsMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646592568162() {
            final IChain.Default chain = new IChain.Default(null);
            assertRequired(() -> chain.mergeIChainLists(null, new IChainList.Default(true)), "source");
            assertRequired(() -> chain.mergeIChainLists(new IChainList.Default(true), null), "target");
        }

        @Test
        @DisplayName("ChainException incompatible types (source)")
        public void test1646592652315() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            assertThrow(() -> chain.mergeIChainLists(new Object(), target))
                    .assertClass(ChainException.class)
                    .assertMessageIs("Received incompatible types to merge\n" +
                                     "Expected type: " + IChainList.class + "\n" +
                                     "Actual source: class java.lang.Object\n" +
                                     "Actual target: " + IChainList.Default.class + "\n");
        }

        @Test
        @DisplayName("ChainException incompatible types (target)")
        public void test1646592726500() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default source = new IChainList.Default(true);
            assertThrow(() -> chain.mergeIChainLists(source, new Object()))
                    .assertClass(ChainException.class)
                    .assertMessageIs("Received incompatible types to merge\n" +
                                     "Expected type: " + IChainList.class + "\n" +
                                     "Actual source: " + IChainList.Default.class + "\n" +
                                     "Actual target: class java.lang.Object\n");
        }

        @Test
        @DisplayName("ChainException incompatible types (ArrayList)")
        public void test1646592819291() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            assertThrow(() -> chain.mergeIChainLists(new ArrayList<>(), target))
                    .assertClass(ChainException.class)
                    .assertMessageIs("Received incompatible types to merge\n" +
                                     "Expected type: " + IChainList.class + "\n" +
                                     "Actual source: class java.util.ArrayList\n" +
                                     "Actual target: " + IChainList.Default.class + "\n");
        }

        @Test
        @DisplayName("ChainException different IChainList types (indexed/unindexed)")
        public void test1646663678542() {
            final IChain.Default chain = new IChain.Default(null);
            assertThrow(() -> chain.mergeIChainLists(chainListOf(true, "foo"), chainListOf(false, "bar")))
                    .assertClass(ChainException.class)
                    .assertMessageIs("Different types of lists are passed for merging.\n" +
                                     "Source list: indexed\n" +
                                     "Target list: not indexed\n");

        }

        @Test
        @DisplayName("return target list if source list is empty (indexed)")
        public void test1646656214559() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            target.add("foo");
            final IChainList.Default source = new IChainList.Default(true);
            final IChainList objects = chain.mergeIChainLists(source, target);
            assertThat(objects).isEqualTo(target);
        }

        @Test
        @DisplayName("return target list if source list is empty (indexed)")
        public void test1646656296614() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            final IChainList.Default source = new IChainList.Default(true);
            source.add("foo");
            final IChainList objects = chain.mergeIChainLists(source, target);
            assertThat(objects).isEqualTo(source);
        }

        @Test
        @DisplayName("return target list if source list is empty (unindexed)")
        public void test1646656276678() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(false);
            target.add("foo");
            final IChainList.Default source = new IChainList.Default(false);
            final IChainList objects = chain.mergeIChainLists(source, target);
            assertThat(objects).isEqualTo(target);
        }

        @Test
        @DisplayName("return target list if source list is empty (unindexed)")
        public void test1646656311847() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(false);
            final IChainList.Default source = new IChainList.Default(false);
            source.add("foo");
            final IChainList objects = chain.mergeIChainLists(source, target);
            assertThat(objects).isEqualTo(source);
        }

        @Test
        @DisplayName("append source list to target list (unindexed)")
        public void test1646663833683() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(false);
            target.add(null);
            target.add("foo");
            final IChainList.Default source = new IChainList.Default(false);
            source.add("bar");
            final IChainList objects = chain.mergeIChainLists(source, target);
            assertThat(objects.toString()).isEqualTo("[null, foo, bar]");
        }

        @Test
        @DisplayName("merge lists if target list length equals source list length (indexed)")
        public void test1646663869647() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList.Default target = new IChainList.Default(true);
            target.add(null);
            target.add("foo");
            final IChainList.Default source = new IChainList.Default(true);
            source.add("bar");
            source.add(null);
            final IChainList objects = chain.mergeIChainLists(source, target);
            assertThat(objects.toString()).isEqualTo("[bar, foo]");
        }

    }

    @Nested
    @DisplayName("#mergeRawMap() method tests")
    public class MergeRawMapMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646595030951() {
            final IChain.Default chain = new IChain.Default(null);
            assertRequired(() -> chain.mergeRawMap(null, mapOf()), "source");
            assertRequired(() -> chain.mergeRawMap(mapOf(), null), "target");
        }

        @Test
        @DisplayName("ChainException incompatible types (source)")
        public void test1646595033381() {
            final IChain.Default chain = new IChain.Default(null);
            assertThrow(() -> chain.mergeRawMap(new Object(), mapOf()))
                    .assertClass(ChainException.class)
                    .assertMessageIs("Received incompatible types to merge\n" +
                                     "Expected type: interface java.util.Map\n" +
                                     "Actual source: class java.lang.Object\n" +
                                     "Actual target: class java.util.HashMap\n");
        }

        @Test
        @DisplayName("ChainException incompatible types (target)")
        public void test1646595036108() {
            final IChain.Default chain = new IChain.Default(null);
            assertThrow(() -> chain.mergeRawMap(mapOf(), new Object()))
                    .assertClass(ChainException.class)
                    .assertMessageIs("Received incompatible types to merge\n" +
                                     "Expected type: interface java.util.Map\n" +
                                     "Actual source: class java.util.HashMap\n" +
                                     "Actual target: class java.lang.Object\n");
        }

        @Test
        @DisplayName("merge plain source and target maps (with different keys)")
        public void test1646595210758() {
            final IChain.Default chain = new IChain.Default(null);
            final Map<String, Object> map = chain.mergeRawMap(mapOf("foo", "foo_value"), mapOf("bar", "bar_value"));
            assertThat(map).hasSize(2);
            assertThat(map.toString()).isEqualTo("{bar=bar_value, foo=foo_value}");
        }

        @Test
        @DisplayName("merge plain source and target maps (with same keys)")
        public void test1646605712727() {
            final IChain.Default chain = new IChain.Default(null);
            final Map<String, Object> map = chain.mergeRawMap(mapOf("foo", "value_1"), mapOf("foo", "value_2"));
            assertThat(map).hasSize(1);
            assertThat(map.toString()).isEqualTo("{foo=[value_2, value_1]}");
        }

        @Test
        @DisplayName("merge source and target maps with nested maps (with different keys)")
        public void test1646605193640() {
            final IChain.Default chain = new IChain.Default(null);
            final Map<String, Object> source = mapOf("foo", mapOf("bar1", "value1"));
            final Map<String, Object> target = mapOf("foo", mapOf("bar2", "value2"));
            final Map<String, Object> map = chain.mergeRawMap(source, target);
            assertThat(map).hasSize(1);
            assertThat(map.toString()).isEqualTo("{foo={bar1=value1, bar2=value2}}");
        }

        @Test
        @DisplayName("merge source and target maps with nested maps (with same keys)")
        public void test1646605911599() {
            final IChain.Default chain = new IChain.Default(null);
            final Map<String, Object> source = mapOf("foo", mapOf("bar", "source_value"));
            final Map<String, Object> target = mapOf("foo", mapOf("bar", "target_value"));
            final Map<String, Object> map = chain.mergeRawMap(source, target);
            assertThat(map).hasSize(1);
            assertThat(map.toString()).isEqualTo("{foo={bar=[target_value, source_value]}}");
        }

        @Test
        @DisplayName("merge source and target maps with nested indexed list (different keys)")
        public void test1646607125585() {
            final IChain.Default chain = new IChain.Default(null);
            final Map<String, Object> source = mapOf("foo", chainListOf(true, "value1"));
            final Map<String, Object> target = mapOf("bar", chainListOf(true, "value2"));
            final Map<String, Object> map = chain.mergeRawMap(source, target);
            assertThat(map.toString()).isEqualTo("{bar=[value2], foo=[value1]}");
        }

        @Test
        @DisplayName("merge source and target maps with nested unindexed list (different keys)")
        public void test1646607373565() {
            final IChain.Default chain = new IChain.Default(null);
            final Map<String, Object> source = mapOf("foo", chainListOf(false, "value1"));
            final Map<String, Object> target = mapOf("bar", chainListOf(false, "value2"));
            final Map<String, Object> map = chain.mergeRawMap(source, target);
            assertThat(map.toString()).isEqualTo("{bar=[value2], foo=[value1]}");
        }

        @Test
        @DisplayName("merge source and target maps with nested indexed list (same keys)")
        public void test1646607394374() {
            final IChain.Default chain = new IChain.Default(null);
            final Map<String, Object> source = mapOf("foo", chainListOf(true, "source_1", "source_2"));
            final Map<String, Object> target = mapOf("foo", chainListOf(true, "target_1", "target_2"));
            final Map<String, Object> map = chain.mergeRawMap(source, target);
            // overwrite target list values by index
            assertThat(map.toString()).isEqualTo("{foo=[source_1, source_2]}");
        }

        @Test
        @DisplayName("merge source and target maps with nested unindexed list (same keys)")
        public void test1646607959613() {
            final IChain.Default chain = new IChain.Default(null);
            final Map<String, Object> source = mapOf("foo", chainListOf(false, "source_1", "source_2"));
            final Map<String, Object> target = mapOf("foo", chainListOf(false, "target_1", "target_2"));
            final Map<String, Object> map = chain.mergeRawMap(source, target);
            // append source list to target list
            assertThat(map.toString()).isEqualTo("{foo=[target_1, target_2, source_1, source_2]}");
        }

    }

    @Nested
    @DisplayName("#mergeObjectValues() method tests")
    public class MergeObjectValuesMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646610637161() {
            final IChain.Default chain = new IChain.Default(null);
            assertRequired(() -> chain.mergeObjectValues(null, mapOf()), "source");
            assertRequired(() -> chain.mergeObjectValues(mapOf(), null), "target");
        }

        @Test
        @DisplayName("Merge IChain indexed lists")
        public void test1646610872564() {
            final IChain.Default chain = new IChain.Default(null);
            final Object o = chain.mergeObjectValues(chainListOf(true, "foo"), chainListOf(true, "bar"));
            // source 'foo' overwrite target 'bar' by index
            assertThat(o.toString()).isEqualTo("[foo]");
        }

        @Test
        @DisplayName("Merge IChain unindexed lists")
        public void test1646611066822() {
            final IChain.Default chain = new IChain.Default(null);
            final Object o = chain.mergeObjectValues(chainListOf(false, "foo"), chainListOf(false, "bar"));
            // source 'foo' append to target 'bar'
            assertThat(o.toString()).isEqualTo("[bar, foo]");
        }

        @Test
        @DisplayName("Merge Maps with simple values (different keys)")
        public void test1646611124532() {
            final IChain.Default chain = new IChain.Default(null);
            final Object o = chain.mergeObjectValues(mapOf("foo", "source"), mapOf("bar", "target"));
            assertThat(o.toString()).isEqualTo("{bar=target, foo=source}");
        }

        @Test
        @DisplayName("Merge Maps with simple values (same keys)")
        public void test1646611226416() {
            final IChain.Default chain = new IChain.Default(null);
            final Object o = chain.mergeObjectValues(mapOf("foo", "source"), mapOf("foo", "target"));
            assertThat(o.toString()).isEqualTo("{foo=[target, source]}");
        }

        @Test
        @DisplayName("Merge simple values")
        public void test1646611373306() {
            final IChain.Default chain = new IChain.Default(null);
            final Object o = chain.mergeObjectValues("source", "target");
            assertThat(o.toString()).isEqualTo("[target, source]");
        }

        @Test
        @DisplayName("Append simple source value to target chain list (unindexed)")
        public void test1646656817452() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList target = chainListOf(false, "foo", "bar");
            final Object o = chain.mergeObjectValues("source", target);
            assertThat(o.toString()).isEqualTo("[foo, bar, source]");
        }

        @Test
        @DisplayName("Append simple target value to source chain list (unindexed)")
        public void test1646657024747() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList source = chainListOf(false, "foo", "bar");
            final Object o = chain.mergeObjectValues(source, "target");
            assertThat(o.toString()).isEqualTo("[target, foo, bar]");
        }

        @Test
        @DisplayName("Append simple source value to target chain list (indexed)")
        public void test1646657153479() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList target = chainListOf(true, "foo", "bar");
            final Object o = chain.mergeObjectValues("source", target);
            assertThat(o.toString()).isEqualTo("[source, bar]");
        }

        @Test
        @DisplayName("Append simple target value to source chain list (indexed)")
        public void test1646657201217() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList source = chainListOf(true, "foo", "bar");
            final Object o = chain.mergeObjectValues(source, "target");
            assertThat(o.toString()).isEqualTo("[target, bar]");
        }

        @Test
        @DisplayName("ChainException incompatible types")
        public void test1646611420769() {
            final IChain.Default chain = new IChain.Default(null);
            assertThrow(() -> chain.mergeObjectValues(mapOf("foo", "source"), chainListOf(true, "target")))
                    .assertClass(ChainException.class)
                    .assertMessageIs("Received incompatible value types to merge.\n" +
                                     "Source type: java.util.HashMap\n" +
                                     "Source value: {foo=source}\n" +
                                     "Target type: " + IChainList.Default.class.getName() + "\n" +
                                     "Target value: [target]\n");
        }
    }

    @Nested
    @DisplayName("#getNewIChainList() method tests")
    public class GetNewIChainListMethodTests {

        @Test
        @DisplayName("New empty list")
        public void test1646611737180() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList list = chain.getNewIChainList(true);
            assertThat(list).isEmpty();
            assertThat(list.isNotIndexed()).isFalse();
        }

        @Test
        @DisplayName("New empty list")
        public void test1646611817471() {
            final IChain.Default chain = new IChain.Default(null);
            final IChainList list = chain.getNewIChainList(false, "foo", "bar");
            assertThat(list).containsExactly("foo", "bar");
            assertThat(list.isNotIndexed()).isTrue();
        }
    }

    @Nested
    @DisplayName("#chainPartsToRawData() method tests")
    public class ChainPartsToRawDataMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646612033683() {
            final IChain.Default chain = new IChain.Default(null);
            assertRequired(() -> chain.chainPartsToRawData(null), "list");
        }

        @Test
        @DisplayName("Return empty map if IChainPart list = []")
        public void test1646612064054() {
            final IChain.Default chain = new IChain.Default(null);
            List<IChainPart> list = new ArrayList<>();
            final Map<String, Object> map = chain.chainPartsToRawData(list);
            assertThat(map).isEmpty();
        }

        @Test
        @DisplayName("Return not empty map if IChainPart list not empty (indexed)")
        public void test1646612149186() {
            final IChainPart.Default f1 = new IChainPart.Default("foo[0][bar][0]", "bar_value_00", true, true);
            final IChainPart.Default f2 = new IChainPart.Default("foo[0][bar][1]", "bar_value_01", true, true);
            final IChainPart.Default f3 = new IChainPart.Default("foo[1][bar][0]", "bar_value_10", true, true);
            final IChainPart.Default f4 = new IChainPart.Default("foo[1][bar][1]", "bar_value_11", true, true);
            final IChain.Default chain = new IChain.Default(null);
            final Map<String, Object> map = chain.chainPartsToRawData(listOf(f1, f2, f3, f4));
            assertThat(map.toString()).isEqualTo("{foo=[" +
                                                 "{bar=[bar_value_00, bar_value_01]}, " +
                                                 "{bar=[bar_value_10, bar_value_11]}" +
                                                 "]}");
        }

        @Test
        @DisplayName("Return not empty map if IChainPart list not empty (mixed index)")
        public void test1646612787824() {
            final IChainPart.Default f1 = new IChainPart.Default("foo[0][bar][]", "bar_value_00", true, true);
            final IChainPart.Default f2 = new IChainPart.Default("foo[0][bar][]", "bar_value_01", true, true);
            final IChainPart.Default f3 = new IChainPart.Default("foo[1][bar][]", "bar_value_10", true, true);
            final IChainPart.Default f4 = new IChainPart.Default("foo[1][bar][]", "bar_value_11", true, true);
            final IChain.Default chain = new IChain.Default(null);
            final Map<String, Object> map = chain.chainPartsToRawData(listOf(f1, f2, f3, f4));
            assertThat(map.toString()).isEqualTo("{foo=[" +
                                                 "{bar=[bar_value_00, bar_value_01]}, " +
                                                 "{bar=[bar_value_10, bar_value_11]}" +
                                                 "]}");
        }

        @Test
        @DisplayName("Return not empty map if IChainPart list not empty (hidden array value)")
        public void test1646612866834() {
            final IChainPart.Default f1 = new IChainPart.Default("foo[0][bar]", "bar_value_00", true, true);
            final IChainPart.Default f2 = new IChainPart.Default("foo[0][bar]", "bar_value_01", true, true);
            final IChainPart.Default f3 = new IChainPart.Default("foo[1][bar]", "bar_value_10", true, true);
            final IChainPart.Default f4 = new IChainPart.Default("foo[1][bar]", "bar_value_11", true, true);
            final IChain.Default chain = new IChain.Default(null);
            final Map<String, Object> map = chain.chainPartsToRawData(listOf(f1, f2, f3, f4));
            assertThat(map.toString()).isEqualTo("{foo=[" +
                                                 "{bar=[bar_value_00, bar_value_01]}, " +
                                                 "{bar=[bar_value_10, bar_value_11]}" +
                                                 "]}");
        }

        @Test
        @DisplayName("Return")
        public void test1646613058807() {
            final IChainPart.Default f1 = new IChainPart.Default("foo[][bar]", "bar_value_00", true, false);
            final IChainPart.Default f2 = new IChainPart.Default("foo[][bar]", "bar_value_01", true, false);
            final IChainPart.Default f3 = new IChainPart.Default("foo[][bar]", "bar_value_10", true, false);
            final IChainPart.Default f4 = new IChainPart.Default("foo[][bar]", "bar_value_11", true, false);
            final IChain.Default chain = new IChain.Default(null);
            final Map<String, Object> map = chain.chainPartsToRawData(listOf(f1, f2, f3, f4));
            assertThat(map.toString()).isEqualTo("{foo=[" +
                                                 "{bar=bar_value_00}, " +
                                                 "{bar=bar_value_01}, " +
                                                 "{bar=bar_value_10}, " +
                                                 "{bar=bar_value_11}" +
                                                 "]}");

        }


    }
}
