package org.touchbit;

import org.touchbit.www.form.urlencoded.marshaller.chain.IChainList;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("SameParameterValue")
public class BaseTest {

    protected static final String ENCODED = "%D1%82%D0%B5%D1%81%D1%82";
    protected static final String DECODED = "тест";

    protected static ThrowableAsserter assertThrow(ThrowableRunnable runnable) {
        return new ThrowableAsserter(runnable);
    }

    protected static void assertUtilityClassException(Class<?> aClass) {
        ThrowableAsserter.assertUtilityClassException(aClass);
    }

    protected void assertNPE(ThrowableRunnable runnable, String parameter) {
        new ThrowableAsserter(runnable).assertNPE(parameter);
    }

    protected static <O> void assertIs(O actual, O expected) {
        assertThat(actual).isEqualTo(expected);
    }

    protected static <O> void assertNotEquals(O actual, O expected) {
        assertThat(actual).isNotEqualTo(expected);
    }

    protected static void assertTrue(Boolean actual) {
        assertThat(actual).isTrue();
    }

    protected static void assertFalse(Boolean actual) {
        assertThat(actual).isFalse();
    }

    protected static <O> void assertNotNull(O actual) {
        assertThat(actual).isNotNull();
    }

    protected static <O> void assertIsNull(O actual) {
        assertThat(actual).isNull();
    }

    @SafeVarargs
    protected static <C> C[] arrayOf(C... items) {
        return items;
    }

    @SafeVarargs
    protected static <C> List<C> listOf(C... items) {
        return Arrays.stream(items).collect(Collectors.toList());
    }

    protected static IChainList chainListOf(boolean isIndexed, Object... items) {
        final IChainList.Default list = new IChainList.Default(isIndexed);
        list.addAll(Arrays.asList(items));
        return list;
    }

    @SafeVarargs
    protected static <C> Set<C> setOf(C... items) {
        return Arrays.stream(items).collect(Collectors.toSet());
    }

    protected static Map<String, Object> mapOf() {
        return new HashMap<>();
    }

    protected static Map<String, Object> mapOf(String k1, Object v1) {
        final Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        return map;
    }

    protected static Map<String, Object> mapOf(String k1, Object v1,
                                               String k2, Object v2) {
        final Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    protected static Map<String, Object> mapOf(String k1, Object v1,
                                               String k2, Object v2,
                                               String k3, Object v3) {
        final Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

}
