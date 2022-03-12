package org.touchbit.www.form.urlencoded.marshaller.chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.www.form.urlencoded.marshaller.BaseTest;

import java.util.List;
import java.util.Map;

@DisplayName("IChainKey.class unit tests")
public class IChainKeyUnitTests extends BaseTest {

    @Nested
    @DisplayName("IChainKey.Default constructor tests")
    public class ChainKeyConstructorTests {

        @Test
        @DisplayName("Create map key")
        public void test1646410703370() {
            final IChainKey.Default key = new IChainKey.Default("foo");
            assertIsNull(key.getIndex());
            assertIs(key.getType(), Map.class);
            assertFalse(key.isIndexedList());
            assertFalse(key.isList());
            assertTrue(key.isMap());
            assertIs(key.getKeyName(), "foo");
            assertIs(key.toString(), "foo-M");
        }

        @Test
        @DisplayName("Create indexed array key")
        public void test1646410902954() {
            final IChainKey.Default key = new IChainKey.Default("0");
            assertIs(key.getIndex(), 0);
            assertIs(key.getType(), List.class);
            assertTrue(key.isIndexedList());
            assertTrue(key.isList());
            assertFalse(key.isMap());
            assertIsNull(key.getKeyName());
            assertIs(key.toString(), "0-L");
        }

        @Test
        @DisplayName("Create unindexed array key (empty value)")
        public void test1646482982278() {
            final IChainKey.Default key = new IChainKey.Default("");
            assertIsNull(key.getIndex());
            assertIs(key.getType(), List.class);
            assertFalse(key.isIndexedList());
            assertTrue(key.isList());
            assertFalse(key.isMap());
            assertIsNull(key.getKeyName());
            assertIs(key.toString(), "L");
        }

        @Test
        @DisplayName("MarshallerException if key is null")
        public void test1646483199660() {
            assertRequired(() -> new IChainKey.Default(null), "key");
        }
    }
}
