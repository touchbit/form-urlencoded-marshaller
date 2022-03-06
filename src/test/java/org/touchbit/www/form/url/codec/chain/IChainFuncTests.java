package org.touchbit.www.form.url.codec.chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.www.form.url.BaseTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IChain.class functional tests")
public class IChainFuncTests extends BaseTest {

    @Test
    @DisplayName("Read data from indexed URL form data")
    public void test1646500418145() {
        final Map<String, Object> actual = new IChain.Default(INDEXED_QUERY_DATA).getRawData();
        IndexedQueryDataMap.match(actual);
    }

    @Test
    @DisplayName("Read data from unindexed URL form data")
    public void test1646500707935() {
        final Map<String, Object> actual = new IChain.Default(UNINDEXED_QUERY_DATA).getRawData();
        UnIndexedQueryDataMap.match(actual);
    }

    public static final String INDEXED_QUERY_DATA = "a_1=aaa&\n" +
                                                    "b_1[0]=bbb&\n" +
                                                    "b_1[2]=ccc&\n" +
                                                    "b_1[3]=ddd&\n" +
                                                    "b_1[1]=eee&\n" +
                                                    "c_1[c_a_2][0][c_a_a_3]=fff&\n" +
                                                    "c_1[c_a_2][0][c_a_b_3][0]=ggg&\n" +
                                                    "c_1[c_a_2][0][c_a_b_3][1]=hhh&\n" +
                                                    "d_1[d_a_2][d_a_a_3]=iii&\n" +
                                                    "d_1[d_a_2][d_a_b_3]=jjj&\n" +
                                                    "c_1[c_a_2][1][c_a_d_3]=kkk&\n" +
                                                    "c_1[c_a_2][1][c_a_c_3][1]=lll&\n" +
                                                    "c_1[c_a_2][1][c_a_c_3][0]=mmm";

    public static final String UNINDEXED_QUERY_DATA = "a_1=aaa&\n" +
                                                      "b_1[]=bbb&\n" +
                                                      "b_1[]=ccc&\n" +
                                                      "b_1[]=ddd&\n" +
                                                      "b_1[]=eee&\n" +
                                                      "c_1[c_a_2][][c_a_a_3]=fff&\n" +
                                                      "c_1[c_a_2][][c_a_b_3][]=ggg&\n" +
                                                      "c_1[c_a_2][][c_a_b_3][]=hhh&\n" +
                                                      "d_1[d_a_2][d_a_a_3]=iii&\n" +
                                                      "d_1[d_a_2][d_a_b_3]=jjj&\n" +
                                                      "c_1[c_a_2][][c_a_d_3]=kkk&\n" +
                                                      "c_1[c_a_2][][c_a_c_3][]=lll&\n" +
                                                      "c_1[c_a_2][][c_a_c_3][]=mmm";

    private static class IndexedQueryDataMap extends HashMap<String, Object> {

        private static final List<Object> b_1;
        private static final HashMap<String, Object> c_1;
        private static final List<Object> c_a_2;
        private static final HashMap<String, Object> c_a_2_map_1;
        private static final HashMap<String, Object> c_a_2_map_2;
        private static final List<Object> c_a_b_3;
        private static final List<Object> c_a_c_3;
        private static final HashMap<String, Object> d_1;
        private static final HashMap<String, Object> d_a_2;

        static {
            b_1 = new ArrayList<>();
            b_1.add("bbb");
            b_1.add("eee");
            b_1.add("ccc");
            b_1.add("ddd");
            c_a_b_3 = new ArrayList<>();
            c_a_b_3.add("ggg");
            c_a_b_3.add("hhh");
            c_a_c_3 = new ArrayList<>();
            c_a_c_3.add("mmm");
            c_a_c_3.add("lll");
            c_a_2_map_1 = new HashMap<>();
            c_a_2_map_1.put("c_a_a_3", "fff");
            c_a_2_map_1.put("c_a_b_3", c_a_b_3);
            c_a_2_map_2 = new HashMap<>();
            c_a_2_map_2.put("c_a_c_3", c_a_c_3);
            c_a_2_map_2.put("c_a_d_3", "kkk");
            c_a_2 = new ArrayList<>();
            c_a_2.add(c_a_2_map_1);
            c_a_2.add(c_a_2_map_2);
            c_1 = new HashMap<>();
            c_1.put("c_a_2", c_a_2);
            d_a_2 = new HashMap<>();
            d_a_2.put("d_a_a_3", "iii");
            d_a_2.put("d_a_b_3", "jjj");
            d_1 = new HashMap<>();
            d_1.put("d_a_2", d_a_2);
        }

        public IndexedQueryDataMap() {
            put("a_1", "aaa");
            put("b_1", b_1);
            put("c_1", c_1);
            put("d_1", d_1);
        }

        @SuppressWarnings("unchecked")
        public static void match(Map<String, Object> actual) {
            final Object act_a_1 = actual.get("a_1");
            assertThat(act_a_1).as("a_1").isEqualTo("aaa");
            final Object act_b_1 = actual.get("b_1");
            assertThat(act_b_1).as(msg(actual, "b_1")).isNotNull();
            assertThat(act_b_1).as(msg(actual, "b_1")).isInstanceOf(List.class);
            assertThat((List<Object>) act_b_1).as(msg(actual, "b_1")).contains("bbb", "eee", "ccc", "ddd");
            final Object act_d_1 = actual.get("d_1");
            assertThat(act_d_1).as(msg(actual, "d_1")).isNotNull();
            assertThat(act_d_1).as(msg(actual, "d_1")).isInstanceOf(Map.class);
            final Map<String, Object> act_d_1_map = (Map<String, Object>) act_d_1;
            final Object act_d_a_2 = act_d_1_map.get("d_a_2");
            assertThat(act_d_a_2).as(msg(actual, "d_a_2")).isNotNull();
            assertThat(act_d_a_2).as(msg(actual, "d_a_2")).isInstanceOf(Map.class);
            final Map<String, Object> act_d_a_2_map = (Map<String, Object>) act_d_a_2;
            assertThat(act_d_a_2_map.get("d_a_a_3")).as(msg(actual, "d_a_a_3")).isEqualTo("iii");
            assertThat(act_d_a_2_map.get("d_a_b_3")).as(msg(actual, "d_a_b_3")).isEqualTo("jjj");
            final Object act_c_1 = actual.get("c_1");
            assertThat(act_c_1).as(msg(actual, "c_1")).isNotNull();
            assertThat(act_c_1).as(msg(actual, "c_1")).isInstanceOf(Map.class);
            final Map<String, Object> act_c_1_map = (Map<String, Object>) act_c_1;
            final Object act_c_a_2 = act_c_1_map.get("c_a_2");
            assertThat(act_c_a_2).as(msg(actual, "c_a_2")).isNotNull();
            assertThat(act_c_a_2).as(msg(actual, "c_a_2")).isInstanceOf(List.class);
            List<Object> act_c_a_2_list = (List<Object>) act_c_a_2;
            assertThat(act_c_a_2_list).as(msg(actual, "c_a_2")).hasSize(2);
            Object act_c_a_2_list_item_0 = act_c_a_2_list.get(0);
            assertThat(act_c_a_2_list_item_0).as(msg(actual, "c_a_2[0]")).isNotNull();
            assertThat(act_c_a_2_list_item_0).as(msg(actual, "c_a_2[0]")).isInstanceOf(Map.class);
            final Map<String, Object> act_c_a_2_list_item_0_map = (Map<String, Object>) act_c_a_2_list_item_0;
            assertThat(act_c_a_2_list_item_0_map.get("c_a_a_3")).as(msg(actual, "c_a_a_3")).isEqualTo("fff");
            assertThat((List<Object>) act_c_a_2_list_item_0_map.get("c_a_b_3")).as(msg(actual, "c_a_b_3")).contains("ggg", "hhh");
            Object act_c_a_2_list_item_1 = act_c_a_2_list.get(1);
            assertThat(act_c_a_2_list_item_1).as(msg(actual, "c_a_2[1]")).isNotNull();
            assertThat(act_c_a_2_list_item_1).as(msg(actual, "c_a_2[1]")).isInstanceOf(Map.class);
            final Map<String, Object> act_c_a_2_list_item_1_map = (Map<String, Object>) act_c_a_2_list_item_1;
            assertThat(act_c_a_2_list_item_1_map.get("c_a_d_3")).as(msg(actual, "c_a_d_3")).isEqualTo("kkk");
            assertThat((List<Object>) act_c_a_2_list_item_1_map.get("c_a_c_3")).as(msg(actual, "c_a_c_3")).contains("mmm", "lll");
        }

        private static String msg(Object act, String field) {
            return "\nExpected: " + new IndexedQueryDataMap() + "\n Actual: " + act + "\n\nField: " + field;
        }

    }

    public static class UnIndexedQueryDataMap extends HashMap<String, Object> {

        private static final String a_1;
        private static final List<Object> b_1;
        private static final HashMap<String, Object> c_1;
        private static final List<Object> c_a_2;
        private static final HashMap<String, Object> c_a_2_map_1;
        private static final List<Object> c_a_b_3;
        private static final List<Object> c_a_c_3;
        private static final HashMap<String, Object> d_1;
        private static final HashMap<String, Object> d_a_2;

        static {
            a_1 = "aaa";
            b_1 = new ArrayList<>();
            b_1.add("bbb");
            b_1.add("ccc");
            b_1.add("ddd");
            b_1.add("eee");
            c_a_b_3 = new ArrayList<>();
            c_a_b_3.add("ggg");
            c_a_b_3.add("hhh");
            c_a_c_3 = new ArrayList<>();
            c_a_c_3.add("lll");
            c_a_c_3.add("mmm");
            c_a_2_map_1 = new HashMap<>();
            c_a_2_map_1.put("c_a_a_3", "fff");
            c_a_2_map_1.put("c_a_b_3", c_a_b_3);
            c_a_2_map_1.put("c_a_c_3", c_a_c_3);
            c_a_2_map_1.put("c_a_d_3", "kkk");
            c_a_2 = new ArrayList<>();
            c_a_2.add(c_a_2_map_1);
            c_1 = new HashMap<>();
            c_1.put("c_a_2", c_a_2);
            d_a_2 = new HashMap<>();
            d_a_2.put("d_a_a_3", "iii");
            d_a_2.put("d_a_b_3", "jjj");
            d_1 = new HashMap<>();
            d_1.put("d_a_2", d_a_2);
        }

        public UnIndexedQueryDataMap() {
            put("a_1", a_1);
            put("b_1", b_1);
            put("c_1", c_1);
            put("d_1", d_1);
        }

        @SuppressWarnings("unchecked")
        public static void match(Map<String, Object> actual) {
            final Object act_a_1 = actual.get("a_1");
            assertThat(act_a_1).as("a_1").isEqualTo("aaa");
            final Object act_b_1 = actual.get("b_1");
            assertThat(act_b_1).as(msg(actual, "b_1")).isNotNull();
            assertThat(act_b_1).as(msg(actual, "b_1")).isInstanceOf(List.class);
            assertThat((List<Object>) act_b_1).as(msg(actual, "b_1")).containsExactlyInAnyOrder("bbb", "eee", "ccc", "ddd");
            final Object act_d_1 = actual.get("d_1");
            assertThat(act_d_1).as(msg(actual, "d_1")).isNotNull();
            assertThat(act_d_1).as(msg(actual, "d_1")).isInstanceOf(Map.class);
            final Map<String, Object> act_d_1_map = (Map<String, Object>) act_d_1;
            final Object act_d_a_2 = act_d_1_map.get("d_a_2");
            assertThat(act_d_a_2).as(msg(actual, "d_a_2")).isNotNull();
            assertThat(act_d_a_2).as(msg(actual, "d_a_2")).isInstanceOf(Map.class);
            final Map<String, Object> act_d_a_2_map = (Map<String, Object>) act_d_a_2;
            assertThat(act_d_a_2_map.get("d_a_a_3")).as(msg(actual, "d_a_a_3")).isEqualTo("iii");
            assertThat(act_d_a_2_map.get("d_a_b_3")).as(msg(actual, "d_a_b_3")).isEqualTo("jjj");
            final Object act_c_1 = actual.get("c_1");
            assertThat(act_c_1).as(msg(actual, "c_1")).isNotNull();
            assertThat(act_c_1).as(msg(actual, "c_1")).isInstanceOf(Map.class);
            final Map<String, Object> act_c_1_map = (Map<String, Object>) act_c_1;
            final Object act_c_a_2 = act_c_1_map.get("c_a_2");
            assertThat(act_c_a_2).as(msg(actual, "c_a_2")).isNotNull();
            assertThat(act_c_a_2).as(msg(actual, "c_a_2")).isInstanceOf(List.class);
            List<Object> act_c_a_2_list = (List<Object>) act_c_a_2;
            assertThat(act_c_a_2_list).as(msg(actual, "c_a_2")).hasSize(1);
            Object act_c_a_2_list_item_0 = act_c_a_2_list.get(0);
            assertThat(act_c_a_2_list_item_0).as(msg(actual, "c_a_2[0]")).isNotNull();
            assertThat(act_c_a_2_list_item_0).as(msg(actual, "c_a_2[0]")).isInstanceOf(Map.class);
            final Map<String, Object> act_c_a_2_list_item_0_map = (Map<String, Object>) act_c_a_2_list_item_0;
            assertThat(act_c_a_2_list_item_0_map.get("c_a_a_3")).as(msg(actual, "c_a_a_3")).isEqualTo("fff");
            assertThat((List<Object>) act_c_a_2_list_item_0_map.get("c_a_b_3")).as(msg(actual, "c_a_b_3")).containsExactlyInAnyOrder("ggg", "hhh");
            assertThat(act_c_a_2_list_item_0_map.get("c_a_d_3")).as(msg(actual, "c_a_d_3")).isEqualTo("kkk");
            assertThat((List<Object>) act_c_a_2_list_item_0_map.get("c_a_c_3")).as(msg(actual, "c_a_c_3")).containsExactlyInAnyOrder("mmm", "lll");
        }
        
        private static String msg(Object act, String field) {
            return "\nExpected: " + new UnIndexedQueryDataMap() + "\n Actual: " + act + "\n\nField: " + field;
        }

    }

}
