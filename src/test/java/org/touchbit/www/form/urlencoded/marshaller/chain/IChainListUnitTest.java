package org.touchbit.www.form.urlencoded.marshaller.chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IChainList.class unit tests")
public class IChainListUnitTest extends BaseTest {

    @Nested
    @DisplayName("#isNotIndexed() method tests")
    public class IsNotIndexedMethodTests {

        @Test
        @DisplayName("return true if isIndexed=false")
        public void test1646408545125() {
            final IChainList.Default chainList = new IChainList.Default(false);
            assertTrue(chainList.isNotIndexed());
        }

        @Test
        @DisplayName("return false if isIndexed=true")
        public void test1646408621757() {
            final IChainList.Default chainList = new IChainList.Default(true);
            assertFalse(chainList.isNotIndexed());
        }
    }

    @Nested
    @DisplayName("#isNotFilled() method tests")
    public class IsNotFilledMethodTests {

        @Test
        @DisplayName("return true if isIndexed=true and list contains nullable values")
        public void test1646408670238() {
            final IChainList.Default chainList = new IChainList.Default(true);
            chainList.add(null);
            assertTrue(chainList.isNotFilled());
        }

        @Test
        @DisplayName("return false if isIndexed=true and list contains values")
        public void test1646408737272() {
            final IChainList.Default chainList = new IChainList.Default(true);
            chainList.add("foo");
            assertFalse(chainList.isNotFilled());
        }


        @Test
        @DisplayName("return true if isIndexed=false and list contains nullable values")
        public void test1646408761416() {
            final IChainList.Default chainList = new IChainList.Default(false);
            chainList.add(null);
            assertTrue(chainList.isNotFilled());
        }

        @Test
        @DisplayName("return true if isIndexed=false and list contains values")
        public void test1646408786291() {
            final IChainList.Default chainList = new IChainList.Default(false);
            chainList.add("foo");
            assertTrue(chainList.isNotFilled());
        }

        @Test
        @DisplayName("return true if isIndexed=false and list is empty")
        public void test1646408803974() {
            final IChainList.Default chainList = new IChainList.Default(false);
            assertTrue(chainList.isNotFilled());
        }

        @Test
        @DisplayName("return false if isIndexed=true and list is empty")
        public void test1646408819585() {
            final IChainList.Default chainList = new IChainList.Default(true);
            assertFalse(chainList.isNotFilled());
        }
    }

    @Nested
    @DisplayName("#equals() method tests")
    public class EqualsMethodTests {

        @Test
        @DisplayName("Use super.equals()")
        public void test1646660270694() {
            final IChainList.Default l1 = new IChainList.Default(true);
            l1.add("foo");
            final IChainList.Default l2 = new IChainList.Default(false);
            l2.add("foo");
            assertThat(l1).isEqualTo(l2);
        }
    }

    @Nested
    @DisplayName("#hashCode() method tests")
    public class HashCodeMethodTests {

        @Test
        @DisplayName("Use super.hashCode()")
        public void test1646660380593() {
            final IChainList.Default l1 = new IChainList.Default(true);
            l1.add("foo");
            final IChainList.Default l2 = new IChainList.Default(false);
            l2.add("foo");
            assertThat(l1.hashCode()).isEqualTo(l2.hashCode());
        }
    }

}
