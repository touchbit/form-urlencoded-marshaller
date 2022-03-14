package qa.benchmark.plan;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import qa.benchmark.plan.model.MapStringString;

@State(Scope.Benchmark)
public class MapStringStringPlan implements IPlan<MapStringStringPlan.Data> {

    @Param({"BYTES_16", "BYTES_32", "BYTES_64", "BYTES_128", "BYTES_256", "BYTES_512", "BYTES_1024"})
    private Data data;

    @Override
    public Data getData() {
        return data;
    }

    public enum Data implements IData {

        BYTES_16(MapStringString.class, MapStringString.POJO_16, MapStringString.DATA_16),
        BYTES_32(MapStringString.class, MapStringString.POJO_32, MapStringString.DATA_32),
        BYTES_64(MapStringString.class, MapStringString.POJO_64, MapStringString.DATA_64),
        BYTES_128(MapStringString.class, MapStringString.POJO_128, MapStringString.DATA_128),
        BYTES_256(MapStringString.class, MapStringString.POJO_256, MapStringString.DATA_256),
        BYTES_512(MapStringString.class, MapStringString.POJO_512, MapStringString.DATA_512),
        BYTES_1024(MapStringString.class, MapStringString.POJO_1024, MapStringString.DATA_1024),
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