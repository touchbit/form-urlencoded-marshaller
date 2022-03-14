package qa.benchmark.plan;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import qa.benchmark.plan.model.POJOFieldTypeInteger;


@State(Scope.Benchmark)
public class POJOIntegerFieldTypePlan implements IPlan<POJOIntegerFieldTypePlan.Data> {

    @Param({"BYTES_16", "BYTES_32", "BYTES_64", "BYTES_128", "BYTES_256", "BYTES_512", "BYTES_1024"})
    public Data data;

    @Override
    public Data getData() {
        return data;
    }

    public enum Data implements IData {

        BYTES_16(POJOFieldTypeInteger.class, POJOFieldTypeInteger.POJO_16, POJOFieldTypeInteger.DATA_16),
        BYTES_32(POJOFieldTypeInteger.class, POJOFieldTypeInteger.POJO_32, POJOFieldTypeInteger.DATA_32),
        BYTES_64(POJOFieldTypeInteger.class, POJOFieldTypeInteger.POJO_64, POJOFieldTypeInteger.DATA_64),
        BYTES_128(POJOFieldTypeInteger.class, POJOFieldTypeInteger.POJO_128, POJOFieldTypeInteger.DATA_128),
        BYTES_256(POJOFieldTypeInteger.class, POJOFieldTypeInteger.POJO_256, POJOFieldTypeInteger.DATA_256),
        BYTES_512(POJOFieldTypeInteger.class, POJOFieldTypeInteger.POJO_512, POJOFieldTypeInteger.DATA_512),
        BYTES_1024(POJOFieldTypeInteger.class, POJOFieldTypeInteger.POJO_1024, POJOFieldTypeInteger.DATA_1024),
        ;

        private final Class<?> pojoClass;
        private final Object pojo;
        private final String urlEncoded;

        Data(Class<?> pojoClass, Object pojo, String urlEncoded) {
            this.pojoClass = pojoClass;
            this.pojo = pojo;
            this.urlEncoded = urlEncoded;
        }

        public Class<?> getPojoClass() {
            return pojoClass;
        }

        public Object getPojo() {
            return pojo;
        }

        public String getURLEncoded() {
            return urlEncoded;
        }
    }

}