package io.m3.util;


import io.m3.test.Tests;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class MathsTest {

    @Test
    void testUtilClass() throws Exception {
        Tests.assertUtilClassIsWellDefined(Maths.class);
    }

    @Test
    void testMul10() {
        assertEquals(1000 * 10, Maths.mul10(1000));
    }

    @Test
    void testMul100() {
        assertEquals(1000 * 100, Maths.mul100(1000));
        assertEquals(1234 * 100, Maths.mul100(1234));
    }

    @Test
    void testDiv10() {
        assertEquals(1000 / 10, Maths.div10(1000));
        assertEquals(990 / 10, Maths.div10(990));
        assertEquals(12 / 10, Maths.div10(12));
        assertEquals(1 / 10, Maths.div10(1));
        assertEquals(123456789 / 10, Maths.div10(123456789));
    }

    @Test
    void testUnsignedDiv10() {
        assertEquals(1000 / 10, Maths.unsignedDiv10(1000));
        assertEquals(990 / 10, Maths.unsignedDiv10(990));
        assertEquals(12 / 10, Maths.unsignedDiv10(12));
        assertEquals(1 / 10, Maths.unsignedDiv10(1));
        assertEquals(123456789 / 10, Maths.unsignedDiv10(123456789));
    }


    @Test
    void testUnsignedDiv1000() {
        assertEquals(1000 / 1000, Maths.unsignedDiv1000(1000));
        assertEquals(90000 / 1000, Maths.unsignedDiv1000(90000));
        assertEquals(12 / 1000, Maths.unsignedDiv1000(12));
        assertEquals(897654 / 1000, Maths.unsignedDiv1000(897654));
    }
}
