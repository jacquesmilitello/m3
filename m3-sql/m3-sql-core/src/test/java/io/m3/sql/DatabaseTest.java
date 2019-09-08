package io.m3.sql;

import io.m3.sql.impl.DatabaseConfigurationException;
import io.m3.sql.impl.DatabaseImpl;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class DatabaseTest {

    @Test
    void simpleTest() {
        DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        assertThrows(DatabaseConfigurationException.class, () -> new DatabaseImpl(ds, Dialect.Name.H2, null, "test", null));
    }

}
