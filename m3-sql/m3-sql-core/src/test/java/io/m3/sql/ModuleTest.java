package io.m3.sql;

import io.m3.sql.model.Pojos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ModuleTest {

    @Test
    void testToString() {
        Module module = new Module("theName", Pojos.DESCRIPTOR_FOLDER) {
        };
        Assertions.assertNotNull(module.toString());
    }

}
