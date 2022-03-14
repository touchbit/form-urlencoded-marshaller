package qa.benchmark.plan;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import qa.benchmark.plan.model.POJOFieldTypeString;

import static qa.benchmark.plan.POJOStringFieldTypePlan.*;

@State(Scope.Benchmark)
public class POJOStringFieldTypePlan implements IPlan<Data> {

    @Param({"BYTES_16", "BYTES_32", "BYTES_64", "BYTES_128", "BYTES_256", "BYTES_512", "BYTES_1024"})
    private Data data;

    @Override
    public Data getData() {
        return data;
    }

    public enum Data implements IData {

        BYTES_16(POJOFieldTypeString.class, POJOFieldTypeString.POJO_16, POJOFieldTypeString.DATA_16),
        BYTES_32(POJOFieldTypeString.class, POJOFieldTypeString.POJO_32, POJOFieldTypeString.DATA_32),
        BYTES_64(POJOFieldTypeString.class, POJOFieldTypeString.POJO_64, POJOFieldTypeString.DATA_64),
        BYTES_128(POJOFieldTypeString.class, POJOFieldTypeString.POJO_128, POJOFieldTypeString.DATA_128),
        BYTES_256(POJOFieldTypeString.class, POJOFieldTypeString.POJO_256, POJOFieldTypeString.DATA_256),
        BYTES_512(POJOFieldTypeString.class, POJOFieldTypeString.POJO_512, POJOFieldTypeString.DATA_512),
        BYTES_1024(POJOFieldTypeString.class, POJOFieldTypeString.POJO_1024, POJOFieldTypeString.DATA_1024),
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