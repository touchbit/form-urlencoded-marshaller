package qa.benchmark.plan.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;

import static org.touchbit.www.form.urlencoded.marshaller.FormUrlMarshaller.INSTANCE;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@FormUrlEncoded
public class POJOFieldTypeInteger extends BasePojo {

    public transient static final String DATA_16 = "offset=0&limit=5";
    public transient static final String DATA_32 = "dptodtrnmk=217&iijisydxhk=184006";
    public transient static final String DATA_64 = "iklpsukark=8122&vsbbhmclhx=92&dptodtrnmk=6627&iijisydxhk=1840506";
    public transient static final String DATA_128 = "iklpsukark=0848122&vsbbhmclhx=830092&dptodtrnmk=662427&iijisydxhk=184056&bvwwiixpjt=082406&sonebdbwoc=7988479&vciosxejvj=0118509";
    public transient static final String DATA_256 = "iklpsukark=084812042&vsbbhmclhx=833330092&dptodtrnmk=218662427&iijisydxhk=184006456&bvwwiixpjt=0826406&sonebdbwoc=7988479&vciosxejvj=0118509&afntjhafmn=5722945&ohnmfoikai=9726713&huzynpfcrd=932500292&cyumjvhfyr=9100782&ejpdnsvycl=3326318&rirfyzeywn=4534539";
    public transient static final String DATA_512 = "iklpsukark=084812042&vsbbhmclhx=833330092&dptodtrnmk=218662427&iijisydxhk=184006456&bvwwiixpjt=082640666&sonebdbwoc=798840679&vciosxejvj=011858109&afntjhafmn=575922945&ohnmfoikai=972126713&huzynpfcrd=932500292&cyumjvhfyr=967100782&ejpdnsvycl=332667318&rirfyzeywn=450434539&ilrfauwewa=018461138&ggzvnqcxzp=687771499&ucwhwqofiv=741337648&ucsepeljjh=042741190&gibzyymkjl=657562803&wdadnbkcyx=3034487&cdiaxfjrcd=2234658&sitzswhmab=125113441&ivxicajbpm=3836270&xgagytibnv=6136516&sffjlqvwgf=3760741&qvhqzqjblm=4354338";
    public transient static final String DATA_1024 = "iklpsukark=084812042&vsbbhmclhx=833330092&dptodtrnmk=218662427&iijisydxhk=184006456&bvwwiixpjt=082640666&sonebdbwoc=798840679&vciosxejvj=011858109&afntjhafmn=575922945&ohnmfoikai=972126713&huzynpfcrd=932500292&cyumjvhfyr=967100782&ejpdnsvycl=332667318&rirfyzeywn=450434539&ilrfauwewa=018461138&ggzvnqcxzp=687771499&ucwhwqofiv=741337648&ucsepeljjh=042741190&gibzyymkjl=657562803&wdadnbkcyx=303463487&cdiaxfjrcd=223463058&sitzswhmab=125113441&ivxicajbpm=383624170&xgagytibnv=613651886&sffjlqvwgf=376840741&qvhqzqjblm=433654338&brzondgeci=884167007&fznlyiotwc=121946095&owjyiuiupj=569233497&dxgoisbwhl=176196947&xzozgzqnqt=645562948&akybbfpuyu=308449270&iabakvwtte=249696783&fctndohhoz=985785496&zdqdznqrcu=744012024&eqtkpxplza=901138916&pysiairljv=629291597&knvzyggpne=293102757&zuvbrnioux=463695207&bikuhlancb=234812426&srunjyqqgu=418503021&cwmczyqzhs=263483412&nqdbyidxre=592006448&uxljiptsrz=613786056&qtzxhawagx=330188671&knjlovqhmi=154222167&paklbibjmj=193919302&eeoondurhe=599260091&afptkryfan=630941627&wxepxvjcub=84178";
    public transient static final POJOFieldTypeInteger POJO_16 = INSTANCE.unmarshal(POJOFieldTypeInteger.class, DATA_16);
    public transient static final POJOFieldTypeInteger POJO_32 = INSTANCE.unmarshal(POJOFieldTypeInteger.class, DATA_32);
    public transient static final POJOFieldTypeInteger POJO_64 = INSTANCE.unmarshal(POJOFieldTypeInteger.class, DATA_64);
    public transient static final POJOFieldTypeInteger POJO_128 = INSTANCE.unmarshal(POJOFieldTypeInteger.class, DATA_128);
    public transient static final POJOFieldTypeInteger POJO_256 = INSTANCE.unmarshal(POJOFieldTypeInteger.class, DATA_256);
    public transient static final POJOFieldTypeInteger POJO_512 = INSTANCE.unmarshal(POJOFieldTypeInteger.class, DATA_512);
    public transient static final POJOFieldTypeInteger POJO_1024 = INSTANCE.unmarshal(POJOFieldTypeInteger.class, DATA_1024);

    @FormUrlEncodedField(FIELD_001_NAME)
    private Integer field_001;
    @FormUrlEncodedField(FIELD_002_NAME)
    private Integer field_002;
    @FormUrlEncodedField(FIELD_003_NAME)
    private Integer field_003;
    @FormUrlEncodedField(FIELD_004_NAME)
    private Integer field_004;
    @FormUrlEncodedField(FIELD_005_NAME)
    private Integer field_005;
    @FormUrlEncodedField(FIELD_006_NAME)
    private Integer field_006;
    @FormUrlEncodedField(FIELD_007_NAME)
    private Integer field_007;
    @FormUrlEncodedField(FIELD_008_NAME)
    private Integer field_008;
    @FormUrlEncodedField(FIELD_009_NAME)
    private Integer field_009;
    @FormUrlEncodedField(FIELD_010_NAME)
    private Integer field_010;
    @FormUrlEncodedField(FIELD_011_NAME)
    private Integer field_011;
    @FormUrlEncodedField(FIELD_012_NAME)
    private Integer field_012;
    @FormUrlEncodedField(FIELD_013_NAME)
    private Integer field_013;
    @FormUrlEncodedField(FIELD_014_NAME)
    private Integer field_014;
    @FormUrlEncodedField(FIELD_015_NAME)
    private Integer field_015;
    @FormUrlEncodedField(FIELD_016_NAME)
    private Integer field_016;
    @FormUrlEncodedField(FIELD_017_NAME)
    private Integer field_017;
    @FormUrlEncodedField(FIELD_018_NAME)
    private Integer field_018;
    @FormUrlEncodedField(FIELD_019_NAME)
    private Integer field_019;
    @FormUrlEncodedField(FIELD_020_NAME)
    private Integer field_020;
    @FormUrlEncodedField(FIELD_021_NAME)
    private Integer field_021;
    @FormUrlEncodedField(FIELD_022_NAME)
    private Integer field_022;
    @FormUrlEncodedField(FIELD_023_NAME)
    private Integer field_023;
    @FormUrlEncodedField(FIELD_024_NAME)
    private Integer field_024;
    @FormUrlEncodedField(FIELD_025_NAME)
    private Integer field_025;
    @FormUrlEncodedField(FIELD_026_NAME)
    private Integer field_026;
    @FormUrlEncodedField(FIELD_027_NAME)
    private Integer field_027;
    @FormUrlEncodedField(FIELD_028_NAME)
    private Integer field_028;
    @FormUrlEncodedField(FIELD_029_NAME)
    private Integer field_029;
    @FormUrlEncodedField(FIELD_030_NAME)
    private Integer field_030;
    @FormUrlEncodedField(FIELD_031_NAME)
    private Integer field_031;
    @FormUrlEncodedField(FIELD_032_NAME)
    private Integer field_032;
    @FormUrlEncodedField(FIELD_033_NAME)
    private Integer field_033;
    @FormUrlEncodedField(FIELD_034_NAME)
    private Integer field_034;
    @FormUrlEncodedField(FIELD_035_NAME)
    private Integer field_035;
    @FormUrlEncodedField(FIELD_036_NAME)
    private Integer field_036;
    @FormUrlEncodedField(FIELD_037_NAME)
    private Integer field_037;
    @FormUrlEncodedField(FIELD_038_NAME)
    private Integer field_038;
    @FormUrlEncodedField(FIELD_039_NAME)
    private Integer field_039;
    @FormUrlEncodedField(FIELD_040_NAME)
    private Integer field_040;
    @FormUrlEncodedField(FIELD_041_NAME)
    private Integer field_041;
    @FormUrlEncodedField(FIELD_042_NAME)
    private Integer field_042;
    @FormUrlEncodedField(FIELD_043_NAME)
    private Integer field_043;
    @FormUrlEncodedField(FIELD_044_NAME)
    private Integer field_044;
    @FormUrlEncodedField(FIELD_045_NAME)
    private Integer field_045;
    @FormUrlEncodedField(FIELD_046_NAME)
    private Integer field_046;
    @FormUrlEncodedField(FIELD_047_NAME)
    private Integer field_047;
    @FormUrlEncodedField(FIELD_048_NAME)
    private Integer field_048;
    @FormUrlEncodedField(FIELD_049_NAME)
    private Integer field_049;
    @FormUrlEncodedField(FIELD_050_NAME)
    private Integer field_050;
    @FormUrlEncodedField(FIELD_051_NAME)
    private Integer field_051;
    @FormUrlEncodedField(FIELD_052_NAME)
    private Integer field_052;
    @FormUrlEncodedField(FIELD_053_NAME)
    private Integer field_053;
    @FormUrlEncodedField(FIELD_054_NAME)
    private Integer field_054;
    @FormUrlEncodedField(FIELD_055_NAME)
    private Integer field_055;
    @FormUrlEncodedField(FIELD_056_NAME)
    private Integer field_056;
    @FormUrlEncodedField(FIELD_057_NAME)
    private Integer field_057;
    @FormUrlEncodedField(FIELD_058_NAME)
    private Integer field_058;
    @FormUrlEncodedField(FIELD_059_NAME)
    private Integer field_059;
    @FormUrlEncodedField(FIELD_060_NAME)
    private Integer field_060;
    @FormUrlEncodedField(FIELD_061_NAME)
    private Integer field_061;
    @FormUrlEncodedField(FIELD_062_NAME)
    private Integer field_062;
    @FormUrlEncodedField(FIELD_063_NAME)
    private Integer field_063;
    @FormUrlEncodedField(FIELD_064_NAME)
    private Integer field_064;
    @FormUrlEncodedField(FIELD_065_NAME)
    private Integer field_065;
    @FormUrlEncodedField(FIELD_066_NAME)
    private Integer field_066;
    @FormUrlEncodedField(FIELD_067_NAME)
    private Integer field_067;
    @FormUrlEncodedField(FIELD_068_NAME)
    private Integer field_068;
    @FormUrlEncodedField(FIELD_069_NAME)
    private Integer field_069;
    @FormUrlEncodedField(FIELD_070_NAME)
    private Integer field_070;
    @FormUrlEncodedField(FIELD_071_NAME)
    private Integer field_071;
    @FormUrlEncodedField(FIELD_072_NAME)
    private Integer field_072;
    @FormUrlEncodedField(FIELD_073_NAME)
    private Integer field_073;
    @FormUrlEncodedField(FIELD_074_NAME)
    private Integer field_074;
    @FormUrlEncodedField(FIELD_075_NAME)
    private Integer field_075;
    @FormUrlEncodedField(FIELD_076_NAME)
    private Integer field_076;
    @FormUrlEncodedField(FIELD_077_NAME)
    private Integer field_077;
    @FormUrlEncodedField(FIELD_078_NAME)
    private Integer field_078;
    @FormUrlEncodedField(FIELD_079_NAME)
    private Integer field_079;
    @FormUrlEncodedField(FIELD_080_NAME)
    private Integer field_080;
    @FormUrlEncodedField(FIELD_081_NAME)
    private Integer field_081;
    @FormUrlEncodedField(FIELD_082_NAME)
    private Integer field_082;
    @FormUrlEncodedField(FIELD_083_NAME)
    private Integer field_083;
    @FormUrlEncodedField(FIELD_084_NAME)
    private Integer field_084;
    @FormUrlEncodedField(FIELD_085_NAME)
    private Integer field_085;
    @FormUrlEncodedField(FIELD_086_NAME)
    private Integer field_086;
    @FormUrlEncodedField(FIELD_087_NAME)
    private Integer field_087;
    @FormUrlEncodedField(FIELD_088_NAME)
    private Integer field_088;
    @FormUrlEncodedField(FIELD_089_NAME)
    private Integer field_089;
    @FormUrlEncodedField(FIELD_090_NAME)
    private Integer field_090;
    @FormUrlEncodedField(FIELD_091_NAME)
    private Integer field_091;
    @FormUrlEncodedField(FIELD_092_NAME)
    private Integer field_092;
    @FormUrlEncodedField(FIELD_093_NAME)
    private Integer field_093;
    @FormUrlEncodedField(FIELD_094_NAME)
    private Integer field_094;
    @FormUrlEncodedField(FIELD_095_NAME)
    private Integer field_095;
    @FormUrlEncodedField(FIELD_096_NAME)
    private Integer field_096;
    @FormUrlEncodedField(FIELD_097_NAME)
    private Integer field_097;
    @FormUrlEncodedField(FIELD_098_NAME)
    private Integer field_098;
    @FormUrlEncodedField(FIELD_099_NAME)
    private Integer field_099;
    @FormUrlEncodedField(FIELD_100_NAME)
    private Integer field_100;

}
