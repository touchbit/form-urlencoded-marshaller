package qa.benchmark.plan;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import qa.benchmark.plan.model.POJOFieldTypeNestedPojo;

@State(Scope.Benchmark)
public class POJOFieldTypeNestedPojoPlan implements IPlan<POJOFieldTypeNestedPojoPlan.Data> {

    @Param({"BYTES_16", "BYTES_32", "BYTES_64", "BYTES_128", "BYTES_256", "BYTES_512", "BYTES_1024"})
    private Data data;

    @Override
    public Data getData() {
        return data;
    }

    public enum Data implements IData {

        BYTES_16(POJOFieldTypeNestedPojo.class, POJOFieldTypeNestedPojo.POJO_16, POJOFieldTypeNestedPojo.DATA_16),
        BYTES_32(POJOFieldTypeNestedPojo.class, POJOFieldTypeNestedPojo.POJO_32, POJOFieldTypeNestedPojo.DATA_32),
        BYTES_64(POJOFieldTypeNestedPojo.class, POJOFieldTypeNestedPojo.POJO_64, POJOFieldTypeNestedPojo.DATA_64),
        BYTES_128(POJOFieldTypeNestedPojo.class, POJOFieldTypeNestedPojo.POJO_128, POJOFieldTypeNestedPojo.DATA_128),
        BYTES_256(POJOFieldTypeNestedPojo.class, POJOFieldTypeNestedPojo.POJO_256, POJOFieldTypeNestedPojo.DATA_256),
        BYTES_512(POJOFieldTypeNestedPojo.class, POJOFieldTypeNestedPojo.POJO_512, POJOFieldTypeNestedPojo.DATA_512),
        BYTES_1024(POJOFieldTypeNestedPojo.class, POJOFieldTypeNestedPojo.POJO_1024, POJOFieldTypeNestedPojo.DATA_1024),
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