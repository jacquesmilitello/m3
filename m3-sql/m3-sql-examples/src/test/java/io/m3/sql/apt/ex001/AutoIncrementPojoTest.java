package io.m3.sql.apt.ex001;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;
import java.sql.Statement;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.m3.sql.Database;
import io.m3.sql.Dialect;
import io.m3.sql.impl.DatabaseImpl;
import io.m3.sql.tx.Transaction;
import io.m3.sql.tx.TransactionManagerImpl;

class AutoIncrementPojoTest {

    private JdbcConnectionPool ds;
    private Database database;

    @BeforeEach
    void before() throws Exception {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1", "sa", "");
        Flyway flyway = Flyway.configure().dataSource(ds).load();
        flyway.migrate();
        database = new DatabaseImpl(ds, Dialect.Name.H2, new TransactionManagerImpl(ds), "", new io.m3.sql.apt.ex001.Module("ex001", ""));
    }

    @AfterEach
    void after() throws Exception {
        try (Connection connection = ds.getConnection()) {
            try (Statement st = connection.createStatement()) {
                st.execute("shutdown");
            }
        }
        ds.dispose();
    }


    @Test
    void test001() throws Exception {

    	AbstractAutoIncrementPojoRepository repository = new AbstractAutoIncrementPojoRepository(database) {
        };

        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
            AutoIncrementPojo pojo = Factory.newAutoIncrementPojo();
            pojo.setName("Jacques");
            repository.insert(pojo);
            tx.commit();
        }

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            AutoIncrementPojo pojo = repository.findById(1);
            assertNotNull(pojo);
            assertEquals("Jacques", pojo.getName());
        }

    }

}