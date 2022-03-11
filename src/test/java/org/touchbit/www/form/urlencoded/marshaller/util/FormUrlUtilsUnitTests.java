package org.touchbit.www.form.urlencoded.marshaller.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.BaseTest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FormUrlUtils.class unit tests")
public class FormUrlUtilsUnitTests extends BaseTest {

    @Nested
    @DisplayName("#encode() method tests")
    public class EncodeMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646678004644() {
            assertRequired(() -> FormUrlUtils.encode(null, UTF_8), "value");
        }

        @Test
        @DisplayName("Encode decoded string")
        public void test1646678016784() {
            assertThat(FormUrlUtils.encode(DECODED, UTF_8)).isEqualTo(ENCODED);
        }

    }

    @Nested
    @DisplayName("#decode() method tests")
    public class DecodeMethodTests {

        @Test
        @DisplayName("Required parameters")
        public void test1646937184822() {
            assertRequired(() -> FormUrlUtils.decode(null, UTF_8), "value");
        }

        @Test
        @DisplayName("Decode encoded string")
        public void test1646677890242() {
            assertThat(FormUrlUtils.decode(ENCODED, UTF_8)).isEqualTo(DECODED);
        }

    }

}
