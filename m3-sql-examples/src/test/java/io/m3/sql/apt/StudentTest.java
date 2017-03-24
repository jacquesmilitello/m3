package io.m3.sql.apt;


import io.m3.sql.Database;
import io.m3.sql.apt.ex001.Factory;
import io.m3.sql.apt.ex001.Student;
import io.m3.sql.apt.ex001.StudentAbstractRepository;
import io.m3.sql.dialect.H2Dialect;
import io.m3.sql.tx.Transaction;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;

import static io.m3.sql.apt.ex001.Factory.newStudent;

public class StudentTest {

    private JdbcConnectionPool ds;
    private Database database;

    @Before
    public void before() throws Exception {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        database = new Database(ds, new H2Dialect(), "");
        RunScript.execute(ds.getConnection(), new InputStreamReader(StudentTest.class.getResourceAsStream("/V00000001__ex001.sql")));

    }

    public void after() throws Exception {
        ds.dispose();
    }


    @Test
    public void test001() throws Exception {

        StudentAbstractRepository repository = new StudentAbstractRepository(database) {
        };

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            Student student = repository.findById(1);
            System.out.println(student);
        }

        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
            Student student = newStudent();
            student.setId(1);
            student.setAge(37);
            repository.insert(student);
            tx.commit();
        }

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            Student student = repository.findById(1);
            System.out.println(student);
        }

        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
            Student student = repository.findById(1);
            student.setAge(38);
            repository.update(student);
            tx.commit();
        }

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            Student student = repository.findById(1);
            System.out.println(student);
        }

    }
}
