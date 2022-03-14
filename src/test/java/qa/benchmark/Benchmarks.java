package qa.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.touchbit.www.form.urlencoded.marshaller.FormUrlMarshaller;
import qa.benchmark.plan.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Benchmarks {

    private static final FormUrlMarshaller MARSHALLER = FormUrlMarshaller.INSTANCE.enableExplicitList();

    public static void main(String[] args) {
        final List<Options> options = benchmarksOptions(
                Map_String_String.class,
                POJO_field_type_String.class,
                POJO_field_type_Integer.class,
                POJO_field_type_List_String.class,
                POJO_field_type_List_Integer.class,
                POJO_field_type_nested_POJO.class
        );
        options.forEach(Benchmarks::run);
    }

    public static class Map_String_String {

        @Benchmark
        public void unmarshal(POJOStringFieldTypePlan plan, Blackhole blackhole) {
            blackhole.consume(MARSHALLER.unmarshal(plan.getData().getPojoClass(), plan.getData().getURLEncoded()));
        }

        @Benchmark
        public void marshal(POJOStringFieldTypePlan plan, Blackhole blackhole) {
            blackhole.consume(MARSHALLER.marshal(plan.getData().getPojo()));
        }

    }

    public static class POJO_field_type_String {

        @Benchmark
        public void unmarshal(POJOStringFieldTypePlan plan, Blackhole blackhole) {
            blackhole.consume(MARSHALLER.unmarshal(plan.getData().getPojoClass(), plan.getData().getURLEncoded()));
        }

        @Benchmark
        public void marshal(POJOStringFieldTypePlan plan, Blackhole blackhole) {
            blackhole.consume(MARSHALLER.marshal(plan.getData().getPojo()));
        }

    }

    public static class POJO_field_type_Integer {

        @Benchmark
        public void unmarshal(POJOIntegerFieldTypePlan plan, Blackhole blackhole) {
            blackhole.consume(MARSHALLER.unmarshal(plan.getData().getPojoClass(), plan.getData().getURLEncoded()));
        }

        @Benchmark
        public void marshal(POJOIntegerFieldTypePlan plan, Blackhole blackhole) {
            blackhole.consume(MARSHALLER.marshal(plan.getData().getPojo()));
        }

    }

    public static class POJO_field_type_List_String {

        @Benchmark
        public void unmarshal(POJOListStringFieldTypePlan plan, Blackhole blackhole) {
            blackhole.consume(MARSHALLER.unmarshal(plan.getData().getPojoClass(), plan.getData().getURLEncoded()));
        }

        @Benchmark
        public void marshal(POJOListStringFieldTypePlan plan, Blackhole blackhole) {
            blackhole.consume(MARSHALLER.marshal(plan.getData().getPojo()));
        }

    }

    public static class POJO_field_type_List_Integer {

        @Benchmark
        public void unmarshal(POJOListIntegerFieldTypePlan plan, Blackhole blackhole) {
            blackhole.consume(MARSHALLER.unmarshal(plan.getData().getPojoClass(), plan.getData().getURLEncoded()));
        }

        @Benchmark
        public void marshal(POJOListIntegerFieldTypePlan plan, Blackhole blackhole) {
            blackhole.consume(MARSHALLER.marshal(plan.getData().getPojo()));
        }

    }

    public static class POJO_field_type_nested_POJO {

        @Benchmark
        public void unmarshal(POJOFieldTypeNestedPojoPlan plan, Blackhole blackhole) {
            blackhole.consume(MARSHALLER.unmarshal(plan.getData().getPojoClass(), plan.getData().getURLEncoded()));
        }

        @Benchmark
        public void marshal(POJOFieldTypeNestedPojoPlan plan, Blackhole blackhole) {
            blackhole.consume(MARSHALLER.marshal(plan.getData().getPojo()));
        }

    }

    private static ChainedOptionsBuilder getDefaultOptionsBuilder() {
        return new OptionsBuilder()
                .threads(4)
                .warmupIterations(5)
                .measurementIterations(5)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .shouldDoGC(true)
                .forks(1)
                .resultFormat(ResultFormatType.JSON);
    }

    private static List<Options> benchmarksOptions(Class<?>... bClass) {
        return Arrays.stream(bClass).map(cls -> getDefaultOptionsBuilder()
                        .include("qa.*." + cls.getSimpleName())
                        .output("target/" + cls.getSimpleName() + "-output.txt")
                        .result(".benchmarks/" + cls.getSimpleName() + "-report.json")
                        .build())
                .collect(Collectors.toList());
    }

    private static void run(Options options) {
        try {
            System.out.println("Output: " + options.getOutput().orElse("console"));
            new Runner(options).run();
            System.out.println("Result: " + options.getResult().orElse("null"));
        } catch (RunnerException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
