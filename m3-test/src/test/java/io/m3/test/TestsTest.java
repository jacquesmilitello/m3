package io.m3.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestsTest {

    @Test
    void testOk() throws Exception {
        Tests.assertUtilClassIsWellDefined(Test01.class);
        Tests.assertUtilClassIsWellDefined(Tests.class);
    }

    @Test
    void testClassNotFinal() {
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> Tests.assertUtilClassIsWellDefined(Test02.class));
        Assertions.assertTrue(error.getMessage().contains("final"));

    }

    @Test
    void testClassPublicAndConstructorPublic() {
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> Tests.assertUtilClassIsWellDefined(Test03.class));
        Assertions.assertTrue(error.getMessage().contains("constructor"));
    }

    @Test
    void testMethodNotStatic() {
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> Tests.assertUtilClassIsWellDefined(Test04.class));
        Assertions.assertTrue(error.getMessage().contains("non-static"));
    }

    private static final class Test01 {
        private Test01() {
        }
    }

    private static class Test02 {
    }

    public final static class Test03 {
    }

    private static final class Test04 {
        private Test04() {
        }

        @SuppressWarnings("unused")
        public void hello() {
        }
    }
}