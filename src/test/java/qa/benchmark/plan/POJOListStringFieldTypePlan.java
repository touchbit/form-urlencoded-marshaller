package qa.benchmark.plan;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import qa.benchmark.plan.model.POJOFieldTypeListString;

@State(Scope.Benchmark)
public class POJOListStringFieldTypePlan implements IPlan<POJOListStringFieldTypePlan.Data> {

    @Param({"BYTES_16", "BYTES_32", "BYTES_64", "BYTES_128", "BYTES_256", "BYTES_512", "BYTES_1024"})
    private Data data;

    @Override
    public Data getData() {
        return data;
    }

    public enum Data implements IData {

        BYTES_16(POJOFieldTypeListString.class, POJOFieldTypeListString.POJO_16, POJOFieldTypeListString.DATA_16),
        BYTES_32(POJOFieldTypeListString.class, POJOFieldTypeListString.POJO_32, POJOFieldTypeListString.DATA_32),
        BYTES_64(POJOFieldTypeListString.class, POJOFieldTypeListString.POJO_64, POJOFieldTypeListString.DATA_64),
        BYTES_128(POJOFieldTypeListString.class, POJOFieldTypeListString.POJO_128, POJOFieldTypeListString.DATA_128),
        BYTES_256(POJOFieldTypeListString.class, POJOFieldTypeListString.POJO_256, POJOFieldTypeListString.DATA_256),
        BYTES_512(POJOFieldTypeListString.class, POJOFieldTypeListString.POJO_512, POJOFieldTypeListString.DATA_512),
        BYTES_1024(POJOFieldTypeListString.class, POJOFieldTypeListString.POJO_1024, POJOFieldTypeListString.DATA_1024),
        ;

        private final Class<?> pojoClass;
        private final Object pojo;
        private final String urlEncoded;

        Data(Class<?> pojoClass, Object pojo, String urlEncoded) {
            this.pojoClass = pojoClass;
            this.pojo = pojo;
            this.urlEncoded = urlEncoded;
        }

        @Override
        public Class<?> getPojoClass() {
            return pojoClass;
        }

        @Override
        public Object getPojo() {
            return pojo;
        }

        @Override
        public String getURLEncoded() {
            return urlEncoded;
        }

    }

}