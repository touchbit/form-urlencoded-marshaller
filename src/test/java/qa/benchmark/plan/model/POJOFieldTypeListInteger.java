package qa.benchmark.plan.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;

import java.util.List;

import static org.touchbit.www.form.urlencoded.marshaller.FormUrlMarshaller.INSTANCE;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@FormUrlEncoded
public class POJOFieldTypeListInteger extends BasePojo {

    public transient static final String DATA_16 = "yvrnlnkeek[0]=34";
    public transient static final String DATA_32 = "yvrnlnkeek[1]=45&yvrnlnkeek[2]=6";
    public transient static final String DATA_64 = "vtxvkeutwe[0]=123460910&vtxvkeutwe[1]=734969160&vtxvkeutwe[2]=35";
    public transient static final String DATA_128 = "iklpsukark[0]=727430953&iklpsukark[1]=438314954&iklpsukark[2]=194193670&vsbbhmclhx[0]=273620501&vsbbhmclhx[1]=15&vsbbhmclhx[2]=5";
    public transient static final String DATA_256 = "dptodtrnmk[0]=515767824&dptodtrnmk[1]=060878011&dptodtrnmk[2]=147926463&iijisydxhk[0]=149923758&iijisydxhk[1]=885853953&iijisydxhk[2]=968270926&bvwwiixpjt[0]=863004941&bvwwiixpjt[1]=135558805&bvwwiixpjt[2]=311983839&sonebdbwoc[0]=426289103&sonebdbwoc[1]=54";
    public transient static final String DATA_512 = "sonebdbwoc[2]=281838117&vciosxejvj[0]=522212845&vciosxejvj[1]=327915665&vciosxejvj[2]=181523633&afntjhafmn[0]=222729969&afntjhafmn[1]=284013105&afntjhafmn[2]=696899459&ohnmfoikai[0]=458950953&ohnmfoikai[1]=193149415&ohnmfoikai[2]=607052170&huzynpfcrd[0]=581207004&huzynpfcrd[1]=556779817&huzynpfcrd[2]=337484545&cyumjvhfyr[0]=206161054&cyumjvhfyr[1]=015825912&cyumjvhfyr[2]=853426226&ejpdnsvycl[0]=180409900&ejpdnsvycl[1]=767482199&ejpdnsvycl[2]=793339900&rirfyzeywn[0]=06791&rirfyzeywn[1]=741&rirfyzeywn[2]=3649";
    public transient static final String DATA_1024 = "ilrfauwewa[0]=632072327&ilrfauwewa[1]=860699023&ilrfauwewa[2]=846960724&ggzvnqcxzp[0]=454006443&ggzvnqcxzp[1]=438860626&ggzvnqcxzp[2]=580245852&ucwhwqofiv[0]=342850696&ucwhwqofiv[1]=285678275&ucwhwqofiv[2]=911824115&ucsepeljjh[0]=259147397&ucsepeljjh[1]=774968250&ucsepeljjh[2]=691477335&gibzyymkjl[0]=628435530&gibzyymkjl[1]=956175020&gibzyymkjl[2]=480493120&wdadnbkcyx[0]=726593355&wdadnbkcyx[1]=530829980&wdadnbkcyx[2]=903476857&cdiaxfjrcd[0]=780779466&cdiaxfjrcd[1]=455437795&cdiaxfjrcd[2]=033838328&sitzswhmab[0]=166037613&sitzswhmab[1]=086290440&sitzswhmab[2]=846026109&ivxicajbpm[0]=375324232&ivxicajbpm[1]=774312457&ivxicajbpm[2]=635346190&xgagytibnv[0]=521694431&xgagytibnv[1]=500795892&xgagytibnv[2]=619984038&sffjlqvwgf[0]=996157762&sffjlqvwgf[1]=346624847&sffjlqvwgf[2]=197062221&qvhqzqjblm[0]=395442324&qvhqzqjblm[1]=852840846&qvhqzqjblm[2]=391857195&brzondgeci[0]=058349624&brzondgeci[1]=573837749&brzondgeci[2]=011285650&fznlyiotwc[0]=160717979&fznlyiotwc[1]=854127052&fznlyiotwc[2]=576256317&owjyiuiupj[0]=20";
    public transient static final POJOFieldTypeListInteger POJO_16 = INSTANCE.unmarshal(POJOFieldTypeListInteger.class, DATA_16);
    public transient static final POJOFieldTypeListInteger POJO_32 = INSTANCE.unmarshal(POJOFieldTypeListInteger.class, DATA_32);
    public transient static final POJOFieldTypeListInteger POJO_64 = INSTANCE.unmarshal(POJOFieldTypeListInteger.class, DATA_64);
    public transient static final POJOFieldTypeListInteger POJO_128 = INSTANCE.unmarshal(POJOFieldTypeListInteger.class, DATA_128);
    public transient static final POJOFieldTypeListInteger POJO_256 = INSTANCE.unmarshal(POJOFieldTypeListInteger.class, DATA_256);
    public transient static final POJOFieldTypeListInteger POJO_512 = INSTANCE.unmarshal(POJOFieldTypeListInteger.class, DATA_512);
    public transient static final POJOFieldTypeListInteger POJO_1024 = INSTANCE.unmarshal(POJOFieldTypeListInteger.class, DATA_1024);

    @FormUrlEncodedField(FIELD_001_NAME)
    private List<String> field_001;
    @FormUrlEncodedField(FIELD_002_NAME)
    private List<String> field_002;
    @FormUrlEncodedField(FIELD_003_NAME)
    private List<String> field_003;
    @FormUrlEncodedField(FIELD_004_NAME)
    private List<String> field_004;
    @FormUrlEncodedField(FIELD_005_NAME)
    private List<String> field_005;
    @FormUrlEncodedField(FIELD_006_NAME)
    private List<String> field_006;
    @FormUrlEncodedField(FIELD_007_NAME)
    private List<String> field_007;
    @FormUrlEncodedField(FIELD_008_NAME)
    private List<String> field_008;
    @FormUrlEncodedField(FIELD_009_NAME)
    private List<String> field_009;
    @FormUrlEncodedField(FIELD_010_NAME)
    private List<String> field_010;
    @FormUrlEncodedField(FIELD_011_NAME)
    private List<String> field_011;
    @FormUrlEncodedField(FIELD_012_NAME)
    private List<String> field_012;
    @FormUrlEncodedField(FIELD_013_NAME)
    private List<String> field_013;
    @FormUrlEncodedField(FIELD_014_NAME)
    private List<String> field_014;
    @FormUrlEncodedField(FIELD_015_NAME)
    private List<String> field_015;
    @FormUrlEncodedField(FIELD_016_NAME)
    private List<String> field_016;
    @FormUrlEncodedField(FIELD_017_NAME)
    private List<String> field_017;
    @FormUrlEncodedField(FIELD_018_NAME)
    private List<String> field_018;
    @FormUrlEncodedField(FIELD_019_NAME)
    private List<String> field_019;
    @FormUrlEncodedField(FIELD_020_NAME)
    private List<String> field_020;
    @FormUrlEncodedField(FIELD_021_NAME)
    private List<String> field_021;
    @FormUrlEncodedField(FIELD_022_NAME)
    private List<String> field_022;
    @FormUrlEncodedField(FIELD_023_NAME)
    private List<String> field_023;
    @FormUrlEncodedField(FIELD_024_NAME)
    private List<String> field_024;
    @FormUrlEncodedField(FIELD_025_NAME)
    private List<String> field_025;
    @FormUrlEncodedField(FIELD_026_NAME)
    private List<String> field_026;
    @FormUrlEncodedField(FIELD_027_NAME)
    private List<String> field_027;
    @FormUrlEncodedField(FIELD_028_NAME)
    private List<String> field_028;
    @FormUrlEncodedField(FIELD_029_NAME)
    private List<String> field_029;
    @FormUrlEncodedField(FIELD_030_NAME)
    private List<String> field_030;
    @FormUrlEncodedField(FIELD_031_NAME)
    private List<String> field_031;
    @FormUrlEncodedField(FIELD_032_NAME)
    private List<String> field_032;
    @FormUrlEncodedField(FIELD_033_NAME)
    private List<String> field_033;
    @FormUrlEncodedField(FIELD_034_NAME)
    private List<String> field_034;
    @FormUrlEncodedField(FIELD_035_NAME)
    private List<String> field_035;
    @FormUrlEncodedField(FIELD_036_NAME)
    private List<String> field_036;
    @FormUrlEncodedField(FIELD_037_NAME)
    private List<String> field_037;
    @FormUrlEncodedField(FIELD_038_NAME)
    private List<String> field_038;
    @FormUrlEncodedField(FIELD_039_NAME)
    private List<String> field_039;
    @FormUrlEncodedField(FIELD_040_NAME)
    private List<String> field_040;
    @FormUrlEncodedField(FIELD_041_NAME)
    private List<String> field_041;
    @FormUrlEncodedField(FIELD_042_NAME)
    private List<String> field_042;
    @FormUrlEncodedField(FIELD_043_NAME)
    private List<String> field_043;
    @FormUrlEncodedField(FIELD_044_NAME)
    private List<String> field_044;
    @FormUrlEncodedField(FIELD_045_NAME)
    private List<String> field_045;
    @FormUrlEncodedField(FIELD_046_NAME)
    private List<String> field_046;
    @FormUrlEncodedField(FIELD_047_NAME)
    private List<String> field_047;
    @FormUrlEncodedField(FIELD_048_NAME)
    private List<String> field_048;
    @FormUrlEncodedField(FIELD_049_NAME)
    private List<String> field_049;
    @FormUrlEncodedField(FIELD_050_NAME)
    private List<String> field_050;
    @FormUrlEncodedField(FIELD_051_NAME)
    private List<String> field_051;
    @FormUrlEncodedField(FIELD_052_NAME)
    private List<String> field_052;
    @FormUrlEncodedField(FIELD_053_NAME)
    private List<String> field_053;
    @FormUrlEncodedField(FIELD_054_NAME)
    private List<String> field_054;
    @FormUrlEncodedField(FIELD_055_NAME)
    private List<String> field_055;
    @FormUrlEncodedField(FIELD_056_NAME)
    private List<String> field_056;
    @FormUrlEncodedField(FIELD_057_NAME)
    private List<String> field_057;
    @FormUrlEncodedField(FIELD_058_NAME)
    private List<String> field_058;
    @FormUrlEncodedField(FIELD_059_NAME)
    private List<String> field_059;
    @FormUrlEncodedField(FIELD_060_NAME)
    private List<String> field_060;
    @FormUrlEncodedField(FIELD_061_NAME)
    private List<String> field_061;
    @FormUrlEncodedField(FIELD_062_NAME)
    private List<String> field_062;
    @FormUrlEncodedField(FIELD_063_NAME)
    private List<String> field_063;
    @FormUrlEncodedField(FIELD_064_NAME)
    private List<String> field_064;
    @FormUrlEncodedField(FIELD_065_NAME)
    private List<String> field_065;
    @FormUrlEncodedField(FIELD_066_NAME)
    private List<String> field_066;
    @FormUrlEncodedField(FIELD_067_NAME)
    private List<String> field_067;
    @FormUrlEncodedField(FIELD_068_NAME)
    private List<String> field_068;
    @FormUrlEncodedField(FIELD_069_NAME)
    private List<String> field_069;
    @FormUrlEncodedField(FIELD_070_NAME)
    private List<String> field_070;
    @FormUrlEncodedField(FIELD_071_NAME)
    private List<String> field_071;
    @FormUrlEncodedField(FIELD_072_NAME)
    private List<String> field_072;
    @FormUrlEncodedField(FIELD_073_NAME)
    private List<String> field_073;
    @FormUrlEncodedField(FIELD_074_NAME)
    private List<String> field_074;
    @FormUrlEncodedField(FIELD_075_NAME)
    private List<String> field_075;
    @FormUrlEncodedField(FIELD_076_NAME)
    private List<String> field_076;
    @FormUrlEncodedField(FIELD_077_NAME)
    private List<String> field_077;
    @FormUrlEncodedField(FIELD_078_NAME)
    private List<String> field_078;
    @FormUrlEncodedField(FIELD_079_NAME)
    private List<String> field_079;
    @FormUrlEncodedField(FIELD_080_NAME)
    private List<String> field_080;
    @FormUrlEncodedField(FIELD_081_NAME)
    private List<String> field_081;
    @FormUrlEncodedField(FIELD_082_NAME)
    private List<String> field_082;
    @FormUrlEncodedField(FIELD_083_NAME)
    private List<String> field_083;
    @FormUrlEncodedField(FIELD_084_NAME)
    private List<String> field_084;
    @FormUrlEncodedField(FIELD_085_NAME)
    private List<String> field_085;
    @FormUrlEncodedField(FIELD_086_NAME)
    private List<String> field_086;
    @FormUrlEncodedField(FIELD_087_NAME)
    private List<String> field_087;
    @FormUrlEncodedField(FIELD_088_NAME)
    private List<String> field_088;
    @FormUrlEncodedField(FIELD_089_NAME)
    private List<String> field_089;
    @FormUrlEncodedField(FIELD_090_NAME)
    private List<String> field_090;
    @FormUrlEncodedField(FIELD_091_NAME)
    private List<String> field_091;
    @FormUrlEncodedField(FIELD_092_NAME)
    private List<String> field_092;
    @FormUrlEncodedField(FIELD_093_NAME)
    private List<String> field_093;
    @FormUrlEncodedField(FIELD_094_NAME)
    private List<String> field_094;
    @FormUrlEncodedField(FIELD_095_NAME)
    private List<String> field_095;
    @FormUrlEncodedField(FIELD_096_NAME)
    private List<String> field_096;
    @FormUrlEncodedField(FIELD_097_NAME)
    private List<String> field_097;
    @FormUrlEncodedField(FIELD_098_NAME)
    private List<String> field_098;
    @FormUrlEncodedField(FIELD_099_NAME)
    private List<String> field_099;
    @FormUrlEncodedField(FIELD_100_NAME)
    private List<String> field_100;

}
