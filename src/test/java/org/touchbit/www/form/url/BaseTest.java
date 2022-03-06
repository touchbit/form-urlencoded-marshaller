package org.touchbit.www.form.url;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.touchbit.test.asserter.ThrowableAsserter;
import org.touchbit.test.asserter.ThrowableRunnable;

import static org.hamcrest.Matchers.*;

public class BaseTest {

    public static ThrowableAsserter assertThrow(ThrowableRunnable runnable) {
        return new ThrowableAsserter(runnable);
    }

    public static void assertUtilityClassException(Class<?> aClass) {
        ThrowableAsserter.assertUtilityClassException(aClass);
    }

    public void assertNPE(ThrowableRunnable runnable, String parameter) {
        new ThrowableAsserter(runnable).assertNPE(parameter);
    }

    public static <T> void assertThat(T actual, Matcher<? super T> matcher) {
        assertThat("", actual, matcher);
    }

    public static <T> void assertThat(String reason, T actual, Matcher<? super T> matcher) {
        MatcherAssert.assertThat(reason, actual, matcher);
    }

    public static <O> void assertIs(O actual, O expected) {
        assertThat(actual, is(expected));
    }

    public static <O> void assertNotEquals(O actual, O expected) {
        assertThat(actual, not(is(expected)));
    }

    public static void assertTrue(Boolean actual) {
        assertThat(actual, is(true));
    }

    public static void assertFalse(Boolean actual) {
        assertThat(actual, is(false));
    }

    public static <O> void assertNotNull(O actual) {
        assertThat(actual, notNullValue());
    }

    public static <O> void assertIsNull(O actual) {
        assertThat(actual, nullValue());
    }

}
