package io.m3.sql.apt;


import io.m3.sql.Database;
import io.m3.sql.apt.ex002.IoM3SqlAptEx002Module;
import io.m3.sql.apt.ex002.Teacher;
import io.m3.sql.apt.ex002.TeacherAbstractRepository;
import io.m3.sql.dialect.H2Dialect;
import io.m3.sql.impl.DatabaseImpl;
import io.m3.sql.jdbc.PreparedStatementSetter;
import io.m3.sql.jdbc.ResultSetMappers;
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

import static io.m3.sql.apt.ex002.Factory.newTeacher;
import static io.m3.sql.apt.ex002.TeacherDescriptor.ID;
import static io.m3.sql.apt.ex002.TeacherDescriptor.TABLE;
import static io.m3.sql.desc.Projections.count;
import static io.m3.util.ImmutableList.of;

public class TeacherTest {

    private JdbcConnectionPool ds;
    private Database database;
    private TeacherRepository repository;

    @Before
    public void before() throws Exception {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        try (Connection connection = ds.getConnection()) {
            RunScript.execute(connection, new InputStreamReader(TeacherTest.class.getResourceAsStream("/V00000001__ex002.sql")));
        }

        database = new DatabaseImpl(ds, new H2Dialect(), "", new IoM3SqlAptEx002Module("ex002", ""));
        repository = new TeacherRepository(database);
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

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            Teacher teacher = repository.findById(1);
            Assert.assertNull(teacher);
        }

        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
            Teacher teacher = newTeacher();
            teacher.setCode("0032");
            teacher.setPrefixCode("ABCD");
            repository.insert(teacher);
            tx.commit();
        }

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            Teacher teacher = repository.findById(1);
            Assert.assertNotNull(teacher);
            Assert.assertEquals("0032", teacher.getCode());
            Assert.assertEquals("ABCD", teacher.getPrefixCode());
            System.out.println(teacher);
        }

        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
            for (int i = 0 ; i < 8192; i++) {
                Teacher teacher = newTeacher();
                teacher.setCode("0032" + i);
                teacher.setPrefixCode("ABCD" + i);
                repository.insert(teacher);
            }
            tx.commit();
        }

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            Assert.assertEquals(8193, repository.countAll());
        }
    }

    @Test
    public void test002() throws Exception {

        // create
        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
            Teacher teacher = newTeacher();
            teacher.setCode("0032");
            teacher.setPrefixCode("ABCD");
            repository.insert(teacher);
            tx.commit();
        }

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            Teacher teacher = repository.findById(1);
            Assert.assertNotNull(teacher);
            Assert.assertNotNull(teacher.getCreationTimestamp());
            Assert.assertNull(teacher.getUpdateTimestamp());
        }

        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
            Teacher teacher = repository.findById(1);
            teacher.setCode("DEFG");
            repository.update(teacher);
            tx.commit();
        }

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            Teacher teacher = repository.findByIdForUpdate(1);
            Assert.assertNotNull(teacher);
            Assert.assertNotNull(teacher.getCreationTimestamp());
            Assert.assertNotNull(teacher.getUpdateTimestamp());
        }

    }

    private static class TeacherRepository extends TeacherAbstractRepository {

        private final String countAll;

        protected TeacherRepository(Database database) {
            super(database);
            countAll = select(of(count(ID))).from(TABLE).build();
        }

        public long countAll() {
            return executeSelect(this.countAll, PreparedStatementSetter.EMPTY, ResultSetMappers.SINGLE_LONG);
        }


    }
}
