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
public class POJOFieldTypeListString extends BasePojo {

    public transient static final String DATA_16 = "lrukgjsnsj[0]=dz";
    public transient static final String DATA_32 = "tlhynvdsil[0]=x&tlhynvdsil[1]=il";
    public transient static final String DATA_64 = "jvxqvwyxcm[0]=zxtxzyz&jvxqvwyxcm[1]=nlqsbbc&jvxqvwyxcm[2]=qbpsae";
    public transient static final String DATA_128 = "vikckxagvh[2]=djxvfctewh&ehgmqxtfgu[0]=uhzmbwyino&ehgmqxtfgu[1]=ezgwwnrelgan&ehgmqxtfgu[2]=oqnrfzwwtnfm&azinqnorhe[0]=aokybdihuo";
    public transient static final String DATA_256 = "piikwikurm[1]=kirykhnjyb&piikwikurm[2]=xdfjtvrseu&mgtjipbulo[0]=uzxpsnvjeq&mgtjipbulo[1]=lxmsgiedeb&mgtjipbulo[2]=dcdlqpdshw&yxflrfhwjb[0]=kwenrjnnjp&yxflrfhwjb[1]=waqixrelrt&yxflrfhwjb[2]=lmwexsveyx&sajfhycycg[0]=bdddfljmqdjw&sajfhycycg[1]=oxinddrznadsdsf";
    public transient static final String DATA_512 = "ejpdnsvycl[2]=gtpmzgespm&rirfyzeywn[0]=ndcwrrvwqg&rirfyzeywn[1]=wjuqhkvtud&rirfyzeywn[2]=sardgttkmf&ilrfauwewa[0]=hsbpaljoup&ilrfauwewa[1]=igldhnrqfa&ilrfauwewa[2]=ojznycreth&ggzvnqcxzp[0]=emwhybpdlq&ggzvnqcxzp[1]=cvuhyvrsmn&ggzvnqcxzp[2]=zpilgwzwfw&ucwhwqofiv[0]=wedakpdbte&ucwhwqofiv[1]=syetthirwa&ucwhwqofiv[2]=ahrmwxiutk&ucsepeljjh[0]=utobqrvdmj&ucsepeljjh[1]=fdjjobxqvd&ucsepeljjh[2]=qkyigmreyj&gibzyymkjl[0]=kooeyrc&gibzyymkjl[1]=bnodcai&gibzyymkjl[2]=muhcazl&wdadnbkcyx[0]=vookrexj&wdadnbkcyx[1]=igivkmfkv";
    public transient static final String DATA_1024 = "yvrnlnkeek[0]=ifyztkxsoe&yvrnlnkeek[1]=kqxccoeydr&yvrnlnkeek[2]=fplzomjlac&vtxvkeutwe[0]=hfufxowzav&vtxvkeutwe[1]=fnohjklzds&vtxvkeutwe[2]=uhlcppqfxk&iklpsukark[0]=axqjpfmhsl&iklpsukark[1]=fntzvfbqhy&iklpsukark[2]=pocodwssnp&vsbbhmclhx[0]=jnvnttocvk&vsbbhmclhx[1]=zsvdfbodji&vsbbhmclhx[2]=turzbopbrj&dptodtrnmk[0]=wrdgpspjys&dptodtrnmk[1]=ejatxyevyh&dptodtrnmk[2]=aimjbmaeud&iijisydxhk[0]=jmsnphccml&iijisydxhk[1]=vadtzsleyn&iijisydxhk[2]=wntatzpybw&bvwwiixpjt[0]=cdldgwpxut&bvwwiixpjt[1]=dbibnammqc&bvwwiixpjt[2]=csqqiybdwg&sonebdbwoc[0]=uqxhetwbeu&sonebdbwoc[1]=qzfjmwaost&sonebdbwoc[2]=hinudbgcaa&vciosxejvj[0]=lllxywkvrn&vciosxejvj[1]=obweusfdok&vciosxejvj[2]=bcqzhlzakh&afntjhafmn[0]=fvntmptasx&afntjhafmn[1]=mhnsmvxngi&afntjhafmn[2]=wjtfwedkcp&ohnmfoikai[0]=vbzibvrwia&ohnmfoikai[1]=jstondjled&ohnmfoikai[2]=ewtjazxvov&huzynpfcrd[0]=gffflnvhix&huzynpfcrd[1]=kmdeftivhq&huzynpfcrd[2]=mmlkrwcnyb&cyumjvhfyr[0]=aprwifybqq&cyumjvhfyr[1]=cppsmmdpfj&cyumjvhfyr[2]=opzcyevdzp&ejpdnsvycl[0]=nkemqtesmj&ejpdnsvycl[1]=lefwlabjjd";
    public transient static final POJOFieldTypeListString POJO_16 = INSTANCE.unmarshal(POJOFieldTypeListString.class, DATA_16);
    public transient static final POJOFieldTypeListString POJO_32 = INSTANCE.unmarshal(POJOFieldTypeListString.class, DATA_32);
    public transient static final POJOFieldTypeListString POJO_64 = INSTANCE.unmarshal(POJOFieldTypeListString.class, DATA_64);
    public transient static final POJOFieldTypeListString POJO_128 = INSTANCE.unmarshal(POJOFieldTypeListString.class, DATA_128);
    public transient static final POJOFieldTypeListString POJO_256 = INSTANCE.unmarshal(POJOFieldTypeListString.class, DATA_256);
    public transient static final POJOFieldTypeListString POJO_512 = INSTANCE.unmarshal(POJOFieldTypeListString.class, DATA_512);
    public transient static final POJOFieldTypeListString POJO_1024 = INSTANCE.unmarshal(POJOFieldTypeListString.class, DATA_1024);

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
