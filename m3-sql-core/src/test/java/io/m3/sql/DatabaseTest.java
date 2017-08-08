package io.m3.sql;

import io.m3.sql.impl.DatabaseImpl;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Test;

import javax.sql.DataSource;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class DatabaseTest {

   // @Test
    public void simpleTest() {

        DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");

        Database database = new DatabaseImpl(ds, null, "test", null);
    }

}
