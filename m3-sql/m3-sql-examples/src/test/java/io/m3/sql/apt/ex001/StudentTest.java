package io.m3.sql.apt.ex001;


import static com.google.common.collect.ImmutableList.of;
import static io.m3.sql.apt.ex001.Factory.newStudent;
import static io.m3.sql.apt.ex001.StudentDescriptor.AGE;
import static io.m3.sql.apt.ex001.StudentDescriptor.ID;
import static io.m3.sql.apt.ex001.StudentDescriptor.TABLE;
import static io.m3.sql.desc.Projections.count;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.m3.sql.Database;
import io.m3.sql.Dialect;
import io.m3.sql.apt.ex001.AbstractStudentRepository;
import io.m3.sql.apt.ex001.Student;
import io.m3.sql.builder.Order;
import io.m3.sql.impl.DatabaseImpl;
import io.m3.sql.jdbc.PreparedStatementSetter;
import io.m3.sql.jdbc.ResultSetMappers;
import io.m3.sql.tx.Transaction;
import io.m3.sql.tx.TransactionManagerImpl;

class StudentTest {

    private JdbcConnectionPool ds;
    private Database database;

    @BeforeEach
    void before() throws Exception {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        database = new DatabaseImpl(ds, Dialect.Name.H2, new TransactionManagerImpl(ds), "", new io.m3.sql.apt.ex001.Module("ex001", ""));
        Flyway flyway = Flyway.configure().dataSource(ds).load();
        flyway.migrate();
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

    	AbstractStudentRepository repository = new AbstractStudentRepository(database) {
        };

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            Student student = repository.findById(1);
            Assertions.assertNull(student);
            System.out.println(student);
        }

        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
            Student student = newStudent();
            student.setId(1);
            student.setAge(37);
            student.setCode("Code1");
            repository.insert(student);
            tx.commit();
        }

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            Student student = repository.findById(1);
            Assertions.assertNotNull(student);
            Assertions.assertEquals(37, student.getAge().intValue());
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
            Assertions.assertNotNull(student);
            Assertions.assertEquals(38, student.getAge().intValue());
            System.out.println(student);
        }

    }

    @Test
    void test002() throws Exception {

        StudentRepository repository = new StudentRepository(database);

        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {

            for (int i = 1; i <= 10; i++) {
                Student student = newStudent();
                student.setId(i);
                student.setAge(37);
                student.setCode("code_" + i);
                repository.insert(student);
            }

            for (int i = 1; i <= 20; i++) {
                Student student = newStudent();
                student.setId(100 + i);
                student.setAge(38);
                student.setCode("code_2_" + i);
                repository.insert(student);
            }

            for (int i = 1; i <= 30; i++) {
                Student student = newStudent();
                student.setId(200 + i);
                student.setAge(39);
                student.setCode("code_3_" + i);
                repository.insert(student);
            }

            tx.commit();
        }

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            Assertions.assertEquals(60, repository.countAll());

            List<Long> values = repository.countGroupByAge();
            Assertions.assertEquals(Long.valueOf(10), values.get(0));
            Assertions.assertEquals(Long.valueOf(20), values.get(1));
            Assertions.assertEquals(Long.valueOf(30), values.get(2));
        }


    }

    private static class StudentRepository extends AbstractStudentRepository {

        private final String countAll;
        private final String countAllGroupBy;

        protected StudentRepository(Database database) {
            super(database);
            countAll = select(of(count(ID))).from(TABLE).build();
            countAllGroupBy = select(of(count(ID))).from(TABLE).groupBy(AGE).orderBy(Order.asc(AGE)).build();
        }

        public long countAll() {
            return executeSelect(this.countAll, PreparedStatementSetter.EMPTY, ResultSetMappers.SINGLE_LONG);
        }

        public List<Long> countGroupByAge() {
            return executeSelect(this.countAllGroupBy, PreparedStatementSetter.EMPTY, (dialect, rs) -> {
                List<Long> result = new ArrayList<>();
                do {
                    result.add(rs.getLong(1));
                } while (rs.next());
                return result;
            });
        }
    }
}
