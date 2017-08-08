package io.m3.sql.apt;


import io.m3.sql.Database;
import io.m3.sql.apt.ex001.AutoIncrementPojo;
import io.m3.sql.apt.ex001.AutoIncrementPojoAbstractRepository;
import io.m3.sql.dialect.H2Dialect;
import io.m3.sql.impl.DatabaseImpl;
import io.m3.sql.tx.Transaction;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

import static io.m3.sql.apt.ex001.Factory.newAutoIncrementPojo;

public class AutoIncrementPojoTest {

    private JdbcConnectionPool ds;
    private Database database;

    @Before
    public void before() throws Exception {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        try (Connection connection = ds.getConnection()) {
            RunScript.execute(connection, new InputStreamReader(AutoIncrementPojoTest.class.getResourceAsStream("/V00000001__ex001.sql")));
        }

        database = new DatabaseImpl(ds, new H2Dialect(), "", new io.m3.sql.apt.ex001.IoM3SqlAptEx001Module("ex001", ""));
    }

    @After
    public void after() throws Exception {
        try (Connection connection = ds.getConnection()) {
            try (Statement st = connection.createStatement()) {
                st.execute("shutdown");
            }
        }
        ds.dispose();
    }


    @Test
    public void test001() throws Exception {

        AutoIncrementPojoAbstractRepository repository = new AutoIncrementPojoAbstractRepository(database) {
        };

        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
            AutoIncrementPojo pojo = newAutoIncrementPojo();
            pojo.setName("Jacques");
            repository.insert(pojo);
            tx.commit();
        }

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            AutoIncrementPojo pojo = repository.findById(1);
            Assert.assertNotNull(pojo);
            Assert.assertEquals("Jacques", pojo.getName());
            System.out.println(pojo);
        }

    }

}
