package qa.benchmark.plan;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import qa.benchmark.plan.model.POJOFieldTypeListInteger;

@State(Scope.Benchmark)
public class POJOListIntegerFieldTypePlan implements IPlan<POJOListIntegerFieldTypePlan.Data> {

    @Param({"BYTES_16", "BYTES_32", "BYTES_64", "BYTES_128", "BYTES_256", "BYTES_512", "BYTES_1024"})
    private Data data;

    @Override
    public Data getData() {
        return data;
    }

    public enum Data implements IData {

        BYTES_16(POJOFieldTypeListInteger.class, POJOFieldTypeListInteger.POJO_16, POJOFieldTypeListInteger.DATA_16),
        BYTES_32(POJOFieldTypeListInteger.class, POJOFieldTypeListInteger.POJO_32, POJOFieldTypeListInteger.DATA_32),
        BYTES_64(POJOFieldTypeListInteger.class, POJOFieldTypeListInteger.POJO_64, POJOFieldTypeListInteger.DATA_64),
        BYTES_128(POJOFieldTypeListInteger.class, POJOFieldTypeListInteger.POJO_128, POJOFieldTypeListInteger.DATA_128),
        BYTES_256(POJOFieldTypeListInteger.class, POJOFieldTypeListInteger.POJO_256, POJOFieldTypeListInteger.DATA_256),
        BYTES_512(POJOFieldTypeListInteger.class, POJOFieldTypeListInteger.POJO_512, POJOFieldTypeListInteger.DATA_512),
        BYTES_1024(POJOFieldTypeListInteger.class, POJOFieldTypeListInteger.POJO_1024, POJOFieldTypeListInteger.DATA_1024),
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