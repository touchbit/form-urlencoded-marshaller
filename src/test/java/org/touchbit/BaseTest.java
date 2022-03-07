package org.touchbit;

import org.touchbit.www.form.urlencoded.marshaller.chain.IChainList;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

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

    public static <O> void assertIs(O actual, O expected) {
        assertThat(actual).isEqualTo(expected);
    }

    public static <O> void assertNotEquals(O actual, O expected) {
        assertThat(actual).isNotEqualTo(expected);
    }

    public static void assertTrue(Boolean actual) {
        assertThat(actual).isTrue();
    }

    public static void assertFalse(Boolean actual) {
        assertThat(actual).isFalse();
    }

    public static <O> void assertNotNull(O actual) {
        assertThat(actual).isNotNull();
    }

    public static <O> void assertIsNull(O actual) {
        assertThat(actual).isNull();
    }

    @SafeVarargs
    public static <C> C[] arrayOf(C... items) {
        return items;
    }

    @SafeVarargs
    public static <C> List<C> listOf(C... items) {
        return Arrays.stream(items).collect(Collectors.toList());
    }

    public static IChainList chainListOf(boolean isIndexed, Object... items) {
        final IChainList.Default list = new IChainList.Default(isIndexed);
        list.addAll(Arrays.asList(items));
        return list;
    }

    @SafeVarargs
    public static <C> Set<C> setOf(C... items) {
        return Arrays.stream(items).collect(Collectors.toSet());
    }

    public static Map<String, Object> mapOf() {
        return new HashMap<>();
    }

    public static Map<String, Object> mapOf(String k1, Object v1) {
        final Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        return map;
    }

    public static Map<String, Object> mapOf(String k1, Object v1,
                                            String k2, Object v2) {
        final Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static Map<String, Object> mapOf(String k1, Object v1,
                                            String k2, Object v2,
                                            String k3, Object v3) {
        final Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

}
