package org.touchbit.www.form.urlencoded.marshaller.chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.www.form.urlencoded.marshaller.BaseTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IChain.class functional tests")
public class IChainFuncTests extends BaseTest {

    @Test
    @DisplayName("Read data from indexed URL form data")
    public void test1646500418145() {
        final Map<String, Object> actual = new IChain.Default(INDEXED_QUERY_DATA).getRawData();
        assertThat(actual.toString()).isEqualTo(INDEXED_MAP_STR);
    }

    @Test
    @DisplayName("Read data from unindexed URL form data")
    public void test1646500707935() {
        final Map<String, Object> actual = new IChain.Default(UNINDEXED_QUERY_DATA).getRawData();
        assertThat(actual.toString()).isEqualTo(UNINDEXED_MAP_STR);
    }

    public static final String INDEXED_QUERY_DATA = "a_1=aaa&\n" +
                                                    "b_1[0]=bbb&\n" +
                                                    "b_1[2]=ccc&\n" +
                                                    "b_1[3]=ddd&\n" +
                                                    "b_1[1]=eee&\n" +
                                                    "c_1[c_a_2][2][c_a_a_3]=fff&\n" +
                                                    "c_1[c_a_2][2][c_a_b_3][0]=ggg&\n" +
                                                    "c_1[c_a_2][2][c_a_b_3][1]=hhh&\n" +
                                                    "d_1[d_a_2][d_a_a_3]=iii&\n" +
                                                    "d_1[d_a_2][d_a_b_3]=jjj&\n" +
                                                    "c_1[c_a_2][0][c_a_d_3]=kkk&\n" +
                                                    "c_1[c_a_2][0][c_a_c_3][2]=lll&\n" +
                                                    "c_1[c_a_2][0][c_a_c_3][1]=mmm&\n" +
                                                    "c_1[c_a_2][0][c_a_f_3]=ooo&\n" +
                                                    "c_1[c_a_2][0][c_a_f_3]=nnn\n";

    public static final String INDEXED_MAP_STR = "{a_1=aaa, " +
                                                 "c_1={c_a_2=[" +
                                                 "{c_a_c_3=[null, mmm, lll], c_a_d_3=kkk, c_a_f_3=[ooo, nnn]}, " +
                                                 "null, " +
                                                 "{c_a_a_3=fff, c_a_b_3=[ggg, hhh]}]}," +
                                                 " b_1=[bbb, eee, ccc, ddd]," +
                                                 " d_1=" +
                                                 "{d_a_2=" +
                                                 "{d_a_b_3=jjj, d_a_a_3=iii}}}";

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

    public static final String UNINDEXED_MAP_STR = "{a_1=aaa, " +
                                                   "c_1={c_a_2=[" +
                                                   "{c_a_a_3=fff, c_a_b_3=[ggg, hhh], c_a_c_3=[lll, mmm], c_a_d_3=kkk}]}, " +
                                                   "b_1=[bbb, ccc, ddd, eee], " +
                                                   "d_1={d_a_2={d_a_b_3=jjj, d_a_a_3=iii}}}";

}
