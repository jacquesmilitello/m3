package io.m3.sql.apt;


import io.m3.sql.Database;
import io.m3.sql.apt.ex001.Factory;
import io.m3.sql.apt.ex001.Student;
import io.m3.sql.apt.ex001.StudentAbstractRepository;
import io.m3.sql.apt.ex001.StudentDescriptor;
import io.m3.sql.builder.Order;
import io.m3.sql.desc.Projections;
import io.m3.sql.dialect.H2Dialect;
import io.m3.sql.impl.DatabaseImpl;
import io.m3.sql.jdbc.PreparedStatementSetter;
import io.m3.sql.jdbc.ResultSetMapper;
import io.m3.sql.jdbc.ResultSetMappers;
import io.m3.sql.tx.Transaction;
import io.m3.util.ImmutableList;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static io.m3.sql.apt.ex001.Factory.newStudent;
import static io.m3.sql.apt.ex001.StudentDescriptor.*;
import static io.m3.sql.apt.ex001.StudentDescriptor.AGE;
import static io.m3.sql.desc.Projections.*;
import static io.m3.util.ImmutableList.of;

public class StudentTest {

    private JdbcConnectionPool ds;
    private Database database;

    @Before
    public void before() throws Exception {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        database = new DatabaseImpl(ds, new H2Dialect(), "");
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

    @Test
    public void test002() throws Exception {

        StudentRepository repository = new StudentRepository(database);

        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {

            for (int i = 1 ; i <= 10 ; i++) {
                Student student = newStudent();
                student.setId(i);
                student.setAge(37);
                repository.insert(student);
            }

            for (int i = 1 ; i <= 20 ; i++) {
                Student student = newStudent();
                student.setId(100+ i);
                student.setAge(38);
                repository.insert(student);
            }

            for (int i = 1 ; i <= 30 ; i++) {
                Student student = newStudent();
                student.setId(200+i );
                student.setAge(39);
                repository.insert(student);
            }

            tx.commit();
        }

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            Assert.assertEquals(60 , repository.countAll());

            List<Long> values = repository.countGroupByAge();
            Assert.assertEquals(Long.valueOf(10), values.get(0));
            Assert.assertEquals(Long.valueOf(20), values.get(1));
            Assert.assertEquals(Long.valueOf(30), values.get(2));
        }



    }

    private static class StudentRepository extends StudentAbstractRepository {

        private final String countAll;
        private final String countAllGroupBy;

        protected StudentRepository(Database database) {
            super(database);
            countAll = select(of(count(ID))).from(TABLE).build();
            countAllGroupBy =  select(of(count(ID))).from(TABLE).groupBy(AGE).orderBy(Order.asc(AGE)).build();
        }

        public long countAll() {
            return executeSelect(this.countAll, PreparedStatementSetter.EMPTY, ResultSetMappers.SINGLE_LONG);
        }

        public List<Long> countGroupByAge() {
            return executeSelect(this.countAllGroupBy, PreparedStatementSetter.EMPTY, rs -> {
                List<Long> result = new ArrayList<>();
                do {
                    result.add(rs.getLong(1));
                } while (rs.next());
                return result;
            });
        }
    }
}
