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
public class POJOFieldTypeNestedPojo extends BasePojo {

    public transient static final String DATA_16 = "offset[offset]=r";
    public transient static final String DATA_32 = "vtxvkeutwe[vtxvkeutwe]=ywlimltgb";
    public transient static final String DATA_64 = "iklpsukark[iklpsukark]=kbnxntsvvt&vsbbhmclhx[vsbbhmclhx]=prnlhnc";
    public transient static final String DATA_128 = "dptodtrnmk[dptodtrnmk]=ogpphlmixw&iijisydxhk[iijisydxhk]=tidwdpuoqy&bvwwiixpjt[bvwwiixpjt]=iqoczvgpch&sonebdbwoc[sonebdbwoc]=lwr";
    public transient static final String DATA_256 = "vciosxejvj[vciosxejvj]=yawszmqpge&afntjhafmn[afntjhafmn]=xygfxwznsl&ohnmfoikai[ohnmfoikai]=ylgpvjhxrz&huzynpfcrd[huzynpfcrd]=laqngnuvij&cyumjvhfyr[cyumjvhfyr]=stahcjyutn&ejpdnsvycl[ejpdnsvycl]=ltwcwexaci&rirfyzeywn[rirfyzeywn]=vuncbfhpzdasdadasdasdasdasdas";
    public transient static final String DATA_512 = "ggzvnqcxzp[ggzvnqcxzp]=btxaryitwm&ucwhwqofiv[ucwhwqofiv]=odhzxslyqq&ucsepeljjh[ucsepeljjh]=bxfbcsptzi&gibzyymkjl[gibzyymkjl]=nlxqtapfos&wdadnbkcyx[wdadnbkcyx]=pgpijwqjxn&cdiaxfjrcd[cdiaxfjrcd]=cckycxrntu&sitzswhmab[sitzswhmab]=frlnuczpcw&ivxicajbpm[ivxicajbpm]=kckzinebmm&xgagytibnv[xgagytibnv]=dbqqqcnujb&sffjlqvwgf[sffjlqvwgf]=rquimvudeu&qvhqzqjblm[qvhqzqjblm]=dpoxsmtcaz&brzondgeci[brzondgeci]=asjqekvxpg&fznlyiotwc[fznlyiotwc]=uvbtqhzohq&owjyiuiupj[owjyiuiupj]=olltkfhwau&dxgoisbwhl[dxgoisbwhl]=oufppuizgmasd";
    public transient static final String DATA_1024 = "akybbfpuyu[akybbfpuyu]=xaflshmztq&iabakvwtte[iabakvwtte]=svzlgoxaox&fctndohhoz[fctndohhoz]=mofvlrbfgf&zdqdznqrcu[zdqdznqrcu]=dfyfixjpan&eqtkpxplza[eqtkpxplza]=yfdwedvvgq&pysiairljv[pysiairljv]=mxpgystaox&knvzyggpne[knvzyggpne]=cnzvtzvhnn&zuvbrnioux[zuvbrnioux]=uazdzzawov&bikuhlancb[bikuhlancb]=oqjzzcniup&srunjyqqgu[srunjyqqgu]=nyvyymstgw&cwmczyqzhs[cwmczyqzhs]=vaimuxahpk&nqdbyidxre[nqdbyidxre]=wwdxwzbecu&uxljiptsrz[uxljiptsrz]=jkmsijaztp&qtzxhawagx[qtzxhawagx]=pludeegmpi&knjlovqhmi[knjlovqhmi]=oqphdwqvrd&paklbibjmj[paklbibjmj]=gbmxamlond&eeoondurhe[eeoondurhe]=igairqscuz&afptkryfan[afptkryfan]=ybuptqchjz&wxepxvjcub[wxepxvjcub]=cimdjfewcs&yztimernln[yztimernln]=ajuqspowld&lwubjpymnq[lwubjpymnq]=bthxujclnu&jynuytrkvb[jynuytrkvb]=zlvdswjcvc&xmgwhnnjrd[xmgwhnnjrd]=eagelpyqug&wfgcwtrdyn[wfgcwtrdyn]=zculzyldnd&lrukgjsnsj[lrukgjsnsj]=qsuzmbptxz&vfdaobtgxp[vfdaobtgxp]=xqtiygweyx&tpzmdtnxdb[tpzmdtnxdb]=aekydsgsne&qhkegtihmr[qhkegtihmr]=xxrvlhacki&ltmqmlfgha[ltmqmlfgha]=rayufxgswy&eigqtfduvy[eigqtfduvy]=ecdkvoldalsadas";
    public transient static final POJOFieldTypeNestedPojo POJO_16 = INSTANCE.unmarshal(POJOFieldTypeNestedPojo.class, DATA_16);
    public transient static final POJOFieldTypeNestedPojo POJO_32 = INSTANCE.unmarshal(POJOFieldTypeNestedPojo.class, DATA_32);
    public transient static final POJOFieldTypeNestedPojo POJO_64 = INSTANCE.unmarshal(POJOFieldTypeNestedPojo.class, DATA_64);
    public transient static final POJOFieldTypeNestedPojo POJO_128 = INSTANCE.unmarshal(POJOFieldTypeNestedPojo.class, DATA_128);
    public transient static final POJOFieldTypeNestedPojo POJO_256 = INSTANCE.unmarshal(POJOFieldTypeNestedPojo.class, DATA_256);
    public transient static final POJOFieldTypeNestedPojo POJO_512 = INSTANCE.unmarshal(POJOFieldTypeNestedPojo.class, DATA_512);
    public transient static final POJOFieldTypeNestedPojo POJO_1024 = INSTANCE.unmarshal(POJOFieldTypeNestedPojo.class, DATA_1024);

    @FormUrlEncodedField(FIELD_001_NAME)
    private POJOFieldTypeString field_001;
    @FormUrlEncodedField(FIELD_002_NAME)
    private POJOFieldTypeString field_002;
    @FormUrlEncodedField(FIELD_003_NAME)
    private POJOFieldTypeString field_003;
    @FormUrlEncodedField(FIELD_004_NAME)
    private POJOFieldTypeString field_004;
    @FormUrlEncodedField(FIELD_005_NAME)
    private POJOFieldTypeString field_005;
    @FormUrlEncodedField(FIELD_006_NAME)
    private POJOFieldTypeString field_006;
    @FormUrlEncodedField(FIELD_007_NAME)
    private POJOFieldTypeString field_007;
    @FormUrlEncodedField(FIELD_008_NAME)
    private POJOFieldTypeString field_008;
    @FormUrlEncodedField(FIELD_009_NAME)
    private POJOFieldTypeString field_009;
    @FormUrlEncodedField(FIELD_010_NAME)
    private POJOFieldTypeString field_010;
    @FormUrlEncodedField(FIELD_011_NAME)
    private POJOFieldTypeString field_011;
    @FormUrlEncodedField(FIELD_012_NAME)
    private POJOFieldTypeString field_012;
    @FormUrlEncodedField(FIELD_013_NAME)
    private POJOFieldTypeString field_013;
    @FormUrlEncodedField(FIELD_014_NAME)
    private POJOFieldTypeString field_014;
    @FormUrlEncodedField(FIELD_015_NAME)
    private POJOFieldTypeString field_015;
    @FormUrlEncodedField(FIELD_016_NAME)
    private POJOFieldTypeString field_016;
    @FormUrlEncodedField(FIELD_017_NAME)
    private POJOFieldTypeString field_017;
    @FormUrlEncodedField(FIELD_018_NAME)
    private POJOFieldTypeString field_018;
    @FormUrlEncodedField(FIELD_019_NAME)
    private POJOFieldTypeString field_019;
    @FormUrlEncodedField(FIELD_020_NAME)
    private POJOFieldTypeString field_020;
    @FormUrlEncodedField(FIELD_021_NAME)
    private POJOFieldTypeString field_021;
    @FormUrlEncodedField(FIELD_022_NAME)
    private POJOFieldTypeString field_022;
    @FormUrlEncodedField(FIELD_023_NAME)
    private POJOFieldTypeString field_023;
    @FormUrlEncodedField(FIELD_024_NAME)
    private POJOFieldTypeString field_024;
    @FormUrlEncodedField(FIELD_025_NAME)
    private POJOFieldTypeString field_025;
    @FormUrlEncodedField(FIELD_026_NAME)
    private POJOFieldTypeString field_026;
    @FormUrlEncodedField(FIELD_027_NAME)
    private POJOFieldTypeString field_027;
    @FormUrlEncodedField(FIELD_028_NAME)
    private POJOFieldTypeString field_028;
    @FormUrlEncodedField(FIELD_029_NAME)
    private POJOFieldTypeString field_029;
    @FormUrlEncodedField(FIELD_030_NAME)
    private POJOFieldTypeString field_030;
    @FormUrlEncodedField(FIELD_031_NAME)
    private POJOFieldTypeString field_031;
    @FormUrlEncodedField(FIELD_032_NAME)
    private POJOFieldTypeString field_032;
    @FormUrlEncodedField(FIELD_033_NAME)
    private POJOFieldTypeString field_033;
    @FormUrlEncodedField(FIELD_034_NAME)
    private POJOFieldTypeString field_034;
    @FormUrlEncodedField(FIELD_035_NAME)
    private POJOFieldTypeString field_035;
    @FormUrlEncodedField(FIELD_036_NAME)
    private POJOFieldTypeString field_036;
    @FormUrlEncodedField(FIELD_037_NAME)
    private POJOFieldTypeString field_037;
    @FormUrlEncodedField(FIELD_038_NAME)
    private POJOFieldTypeString field_038;
    @FormUrlEncodedField(FIELD_039_NAME)
    private POJOFieldTypeString field_039;
    @FormUrlEncodedField(FIELD_040_NAME)
    private POJOFieldTypeString field_040;
    @FormUrlEncodedField(FIELD_041_NAME)
    private POJOFieldTypeString field_041;
    @FormUrlEncodedField(FIELD_042_NAME)
    private POJOFieldTypeString field_042;
    @FormUrlEncodedField(FIELD_043_NAME)
    private POJOFieldTypeString field_043;
    @FormUrlEncodedField(FIELD_044_NAME)
    private POJOFieldTypeString field_044;
    @FormUrlEncodedField(FIELD_045_NAME)
    private POJOFieldTypeString field_045;
    @FormUrlEncodedField(FIELD_046_NAME)
    private POJOFieldTypeString field_046;
    @FormUrlEncodedField(FIELD_047_NAME)
    private POJOFieldTypeString field_047;
    @FormUrlEncodedField(FIELD_048_NAME)
    private POJOFieldTypeString field_048;
    @FormUrlEncodedField(FIELD_049_NAME)
    private POJOFieldTypeString field_049;
    @FormUrlEncodedField(FIELD_050_NAME)
    private POJOFieldTypeString field_050;
    @FormUrlEncodedField(FIELD_051_NAME)
    private POJOFieldTypeString field_051;
    @FormUrlEncodedField(FIELD_052_NAME)
    private POJOFieldTypeString field_052;
    @FormUrlEncodedField(FIELD_053_NAME)
    private POJOFieldTypeString field_053;
    @FormUrlEncodedField(FIELD_054_NAME)
    private POJOFieldTypeString field_054;
    @FormUrlEncodedField(FIELD_055_NAME)
    private POJOFieldTypeString field_055;
    @FormUrlEncodedField(FIELD_056_NAME)
    private POJOFieldTypeString field_056;
    @FormUrlEncodedField(FIELD_057_NAME)
    private POJOFieldTypeString field_057;
    @FormUrlEncodedField(FIELD_058_NAME)
    private POJOFieldTypeString field_058;
    @FormUrlEncodedField(FIELD_059_NAME)
    private POJOFieldTypeString field_059;
    @FormUrlEncodedField(FIELD_060_NAME)
    private POJOFieldTypeString field_060;
    @FormUrlEncodedField(FIELD_061_NAME)
    private POJOFieldTypeString field_061;
    @FormUrlEncodedField(FIELD_062_NAME)
    private POJOFieldTypeString field_062;
    @FormUrlEncodedField(FIELD_063_NAME)
    private POJOFieldTypeString field_063;
    @FormUrlEncodedField(FIELD_064_NAME)
    private POJOFieldTypeString field_064;
    @FormUrlEncodedField(FIELD_065_NAME)
    private POJOFieldTypeString field_065;
    @FormUrlEncodedField(FIELD_066_NAME)
    private POJOFieldTypeString field_066;
    @FormUrlEncodedField(FIELD_067_NAME)
    private POJOFieldTypeString field_067;
    @FormUrlEncodedField(FIELD_068_NAME)
    private POJOFieldTypeString field_068;
    @FormUrlEncodedField(FIELD_069_NAME)
    private POJOFieldTypeString field_069;
    @FormUrlEncodedField(FIELD_070_NAME)
    private POJOFieldTypeString field_070;
    @FormUrlEncodedField(FIELD_071_NAME)
    private POJOFieldTypeString field_071;
    @FormUrlEncodedField(FIELD_072_NAME)
    private POJOFieldTypeString field_072;
    @FormUrlEncodedField(FIELD_073_NAME)
    private POJOFieldTypeString field_073;
    @FormUrlEncodedField(FIELD_074_NAME)
    private POJOFieldTypeString field_074;
    @FormUrlEncodedField(FIELD_075_NAME)
    private POJOFieldTypeString field_075;
    @FormUrlEncodedField(FIELD_076_NAME)
    private POJOFieldTypeString field_076;
    @FormUrlEncodedField(FIELD_077_NAME)
    private POJOFieldTypeString field_077;
    @FormUrlEncodedField(FIELD_078_NAME)
    private POJOFieldTypeString field_078;
    @FormUrlEncodedField(FIELD_079_NAME)
    private POJOFieldTypeString field_079;
    @FormUrlEncodedField(FIELD_080_NAME)
    private POJOFieldTypeString field_080;
    @FormUrlEncodedField(FIELD_081_NAME)
    private POJOFieldTypeString field_081;
    @FormUrlEncodedField(FIELD_082_NAME)
    private POJOFieldTypeString field_082;
    @FormUrlEncodedField(FIELD_083_NAME)
    private POJOFieldTypeString field_083;
    @FormUrlEncodedField(FIELD_084_NAME)
    private POJOFieldTypeString field_084;
    @FormUrlEncodedField(FIELD_085_NAME)
    private POJOFieldTypeString field_085;
    @FormUrlEncodedField(FIELD_086_NAME)
    private POJOFieldTypeString field_086;
    @FormUrlEncodedField(FIELD_087_NAME)
    private POJOFieldTypeString field_087;
    @FormUrlEncodedField(FIELD_088_NAME)
    private POJOFieldTypeString field_088;
    @FormUrlEncodedField(FIELD_089_NAME)
    private POJOFieldTypeString field_089;
    @FormUrlEncodedField(FIELD_090_NAME)
    private POJOFieldTypeString field_090;
    @FormUrlEncodedField(FIELD_091_NAME)
    private POJOFieldTypeString field_091;
    @FormUrlEncodedField(FIELD_092_NAME)
    private POJOFieldTypeString field_092;
    @FormUrlEncodedField(FIELD_093_NAME)
    private POJOFieldTypeString field_093;
    @FormUrlEncodedField(FIELD_094_NAME)
    private POJOFieldTypeString field_094;
    @FormUrlEncodedField(FIELD_095_NAME)
    private POJOFieldTypeString field_095;
    @FormUrlEncodedField(FIELD_096_NAME)
    private POJOFieldTypeString field_096;
    @FormUrlEncodedField(FIELD_097_NAME)
    private POJOFieldTypeString field_097;
    @FormUrlEncodedField(FIELD_098_NAME)
    private POJOFieldTypeString field_098;
    @FormUrlEncodedField(FIELD_099_NAME)
    private POJOFieldTypeString field_099;
    @FormUrlEncodedField(FIELD_100_NAME)
    private POJOFieldTypeString field_100;

}
