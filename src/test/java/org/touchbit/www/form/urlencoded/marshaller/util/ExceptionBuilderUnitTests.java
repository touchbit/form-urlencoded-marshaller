package org.touchbit.www.form.urlencoded.marshaller.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.touchbit.www.form.urlencoded.marshaller.BaseTest;
import qa.model.FieldsClass;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ExceptionBuilder.class unit tests")
public class ExceptionBuilderUnitTests extends BaseTest {

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    @Nested
    @DisplayName("#errorMessage(String) method tests")
    public class ErrorMessageMethodTests {

        @Test
        @DisplayName("errorMessage = foo")
        public void test1647017270896() {
            final String msg = builder().errorMessage("foo").build().getMessage();
            assertIs(msg, "\n  foo\n");
        }

        @Test
        @DisplayName("errorMessage = null")
        public void test1647017490360() {
            final String msg = builder().errorMessage(null).build().getMessage();
            assertIs(msg, "\n  null\n");
        }

    }

    @Nested
    @DisplayName("#errorCause() method tests")
    public class ErrorCauseMethodTests {

        @Test
        @DisplayName("errorCause is null")
        public void test1647022500123() {
            String err = builder().errorCause(null).getAdditionalInfo().toString();
            assertIs(err, "    Error cause: <absent>");
        }

        @Test
        @DisplayName("errorCause without internal Exceptions")
        public void test1647022515347() {
            String err = builder().errorCause(new RuntimeException("foo")).getAdditionalInfo().toString();
            assertIs(err, "    Error cause: foo");
        }

        @Test
        @DisplayName("errorCause without internal Exceptions")
        public void test1647022536096() {
            String err = builder().errorCause(new RuntimeException("foo", new RuntimeException("bar")))
                    .getAdditionalInfo().toString();
            assertIs(err, "    Error cause: \n" +
                          "     - foo\n" +
                          "     - bar");
        }

    }

    @Nested
    @DisplayName("#getNestedCauses() method tests")
    public class GetNestedCausesMethodTests {

        @Test
        @DisplayName("throwable is null")
        public void test1647022500123() {
            List<Throwable> err = builder().getNestedCauses(null);
            assertThat(err).isEmpty();
        }

        @Test
        @DisplayName("throwable without internal Exceptions")
        public void test1647022515347() {
            final RuntimeException bar = new RuntimeException("bar");
            List<Throwable> err = builder().getNestedCauses(bar);
            assertThat(err).containsExactly(bar);
        }

        @Test
        @DisplayName("throwable without internal Exceptions")
        public void test1647022536096() {
            final RuntimeException bar = new RuntimeException("bar");
            final RuntimeException foo = new RuntimeException("foo", bar);
            List<Throwable> err = builder().getNestedCauses(foo);
            assertThat(err).containsExactly(foo, bar);
        }

    }

    @ParameterizedTest
    @MethodSource("provideTest1647018980645")
    @DisplayName("Additional info test")
    public void test1647018980645(String actual, String expected) {
        assertIs(actual, expected);
    }

    private static Stream<Arguments> provideTest1647018980645() {
        return Stream.of(
                Arguments.of(aInfo(builder()::targetType, Object.class), "    Target type: java.lang.Object"),
                Arguments.of(aInfo(builder()::targetType, null), "    Target type: null"),
                Arguments.of(aInfo(builder()::sourceType, new Object()), "    Source type: java.lang.Object"),
                Arguments.of(aInfo(builder()::sourceType, null), "    Source type: null"),
                Arguments.of(aInfo(builder()::sourceType, Object.class), "    Source type: java.lang.Object"),
                Arguments.of(aInfo(builder()::sourceType, (Type) null), "    Source type: null"),
                Arguments.of(aInfo(builder()::actualType, new Object()), "    Actual type: java.lang.Object"),
                Arguments.of(aInfo(builder()::actualType, null), "    Actual type: null"),
                Arguments.of(aInfo(builder()::actualType, Object.class), "    Actual type: java.lang.Object"),
                Arguments.of(aInfo(builder()::actualType, (Type) null), "    Actual type: null"),
                Arguments.of(aInfo(builder()::valueType, new Object()), "    Value type: java.lang.Object"),
                Arguments.of(aInfo(builder()::valueType, null), "    Value type: null"),
                Arguments.of(aInfo(builder()::valueType, Object.class), "    Value type: java.lang.Object"),
                Arguments.of(aInfo(builder()::valueType, (Type) null), "    Value type: null"),
                Arguments.of(aInfo(builder()::model, new Object()), "    Model: java.lang.Object"),
                Arguments.of(aInfo(builder()::model, null), "    Model: null"),
                Arguments.of(aInfo(builder()::model, Object.class), "    Model: java.lang.Object"),
                Arguments.of(aInfo(builder()::model, (Type) null), "    Model: null"),
                Arguments.of(aInfo(builder()::annotation, Test.class), "    Annotation: @Test"),
                Arguments.of(aInfo(builder()::annotation, null), "    Annotation: @null"),
                Arguments.of(aInfo(builder()::annotationType, Test.class), "    Annotation type: org.junit.jupiter.api.Test"),
                Arguments.of(aInfo(builder()::annotationType, null), "    Annotation type: null"),
                Arguments.of(aInfo(builder()::fields, FieldsClass.getFields()), "    Fields:\n" +
                                                                                "     - private String foo;\n" +
                                                                                "     - private String bar;"),
                Arguments.of(aInfo(builder()::fields, FieldsClass.getOneFieldList()), "    Fields:\n" +
                                                                                      "     - private String foo;"),
                Arguments.of(aInfo(builder()::fields, new ArrayList<>()), "    Fields: <absent>"),
                Arguments.of(aInfo(builder()::fields, null), "    Fields: <absent>"),
                Arguments.of(aInfo(builder()::targetField, FieldsClass.foo()), "    Target field: private String foo;"),
                Arguments.of(aInfo(builder()::targetField, null), "    Target field: null"),
                Arguments.of(aInfo(builder()::field, FieldsClass.foo()), "    Field: private String foo;"),
                Arguments.of(aInfo(builder()::field, null), "    Field: null"),
                Arguments.of(aInfo(builder()::value, UTF_8), "    Value: UTF-8"),
                Arguments.of(aInfo(builder()::value, null), "    Value: null"),
                Arguments.of(aInfo(builder()::actual, UTF_8), "    Actual: UTF-8"),
                Arguments.of(aInfo(builder()::actual, null), "    Actual: null"),
                Arguments.of(aInfo(builder()::actualValue, UTF_8), "    Actual value: UTF-8"),
                Arguments.of(aInfo(builder()::actualValue, null), "    Actual value: null"),
                Arguments.of(aInfo(builder()::sourceField, "foo"), "    Source field: foo"),
                Arguments.of(aInfo(builder()::sourceField, null), "    Source field: null"),
                Arguments.of(aInfo(builder()::sourceValue, "foo"), "    Source value: foo"),
                Arguments.of(aInfo(builder()::sourceValue, arrayOf("foo", "bar")), "    Source value: [foo, bar]"),
                Arguments.of(aInfo(builder()::sourceValue, null), "    Source value: null"),
                Arguments.of(aInfo(builder()::source, UTF_8), "    Source: UTF-8"),
                Arguments.of(aInfo(builder()::source, null), "    Source: null"),
                Arguments.of(aInfo(builder()::expected, UTF_8), "    Expected: UTF-8"),
                Arguments.of(aInfo(builder()::expected, null), "    Expected: null"),
                Arguments.of(aInfo(builder()::expectedType, Object.class), "    Expected type: java.lang.Object"),
                Arguments.of(aInfo(builder()::expectedType, null), "    Expected type: null"),
                Arguments.of(aInfo(builder()::expectedHeirsOf, Object.class), "    Expected: heirs of java.lang.Object"),
                Arguments.of(aInfo(builder()::expectedHeirsOf, null), "    Expected: heirs of null"),
                Arguments.of(aInfo(builder()::expectedValue, null), "    Expected value: null"),
                Arguments.of(aInfo(builder()::expectedValue, new HashMap<>()), "    Expected value: {}")
        );
    }

    private static <V> String aInfo(Function<V, ExceptionBuilder<?>> function, V value) {
        return function.apply(value).getAdditionalInfo().toString();
    }

    private static ExceptionBuilder<RuntimeException> builder() {
        return new ExceptionBuilder<RuntimeException>() {
            @Override
            public RuntimeException build() {
                return new RuntimeException(getMessage(), getCause());
            }
        };
    }

}
