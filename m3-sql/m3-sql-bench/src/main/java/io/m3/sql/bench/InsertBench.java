package io.m3.sql.bench;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import io.m3.sql.Database;
import io.m3.sql.Dialect;
import io.m3.sql.bench.pojo.AbstractPersonRepository;
import io.m3.sql.bench.pojo.Factory;
import io.m3.sql.bench.pojo.Person;
import io.m3.sql.bench.pojo.PersonJPA;
import io.m3.sql.impl.DatabaseImpl;
import io.m3.sql.tx.Transaction;
import io.m3.sql.tx.TransactionManagerImpl;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
public class InsertBench {

    private static final DataSource DATA_SOURCE;

    private static final SessionFactory SESSION_FACTORY;

    private static final Database DATABASE;

    private static final AbstractPersonRepository PERSON_ABSTRACT_REPOSITORY;

    private static final int MAX = 10000;

    static {
        JdbcConnectionPool ds = JdbcConnectionPool.create("jdbc:h2:mem:bench;DB_CLOSE_DELAY=-1", "sa", "");
        DATA_SOURCE = ds;
        try {
            RunScript.execute(ds.getConnection(), new InputStreamReader(InsertBench.class.getResourceAsStream("/bench.sql")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
        serviceRegistryBuilder.applySetting(Environment.DATASOURCE, ds);

        SESSION_FACTORY = new Configuration()
                .addAnnotatedClass(PersonJPA.class)
                .buildSessionFactory(serviceRegistryBuilder.build());

        DATABASE = new DatabaseImpl(ds, Dialect.Name.H2, new TransactionManagerImpl(ds), "", new io.m3.sql.bench.pojo.Module("bench", ""));

        PERSON_ABSTRACT_REPOSITORY = new AbstractPersonRepository(DATABASE) {
        };
    }

    @Test
    public void dotest() throws Exception {
        Options opt = new OptionsBuilder().include(InsertBench.class.getSimpleName()).forks(1).jvmArgs("-server", "-XX:+AggressiveOpts", "-XX:+UseFastAccessorMethods", "-XX:+UseG1GC")
                .verbosity(VerboseMode.EXTRA)
                .shouldDoGC(true)
                .build();
        new Runner(opt).run();
    }

    private void reset() {

        try (Connection connection = DATA_SOURCE.getConnection()) {
            RunScript.execute(connection, new InputStreamReader(InsertBench.class.getResourceAsStream("/reset.sql")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    public void hibernate() {

        reset();

        for (int i = 0; i < MAX; i++) {
            try (Session session = SESSION_FACTORY.openSession()) {
                PersonJPA person = new PersonJPA();
                person.setFirstName("first_name");
                person.setLastName("first_name");
                person.setId(i);
                session.beginTransaction();
                session.save(person);
                session.getTransaction().commit();

            }
        }
    }

    @Benchmark
    public void hibernateOneTx() {

        reset();

        try (Session session = SESSION_FACTORY.openSession()) {

            session.beginTransaction();

            for (int i = 0; i < MAX; i++) {
                PersonJPA person = new PersonJPA();
                person.setFirstName("first_name");
                person.setLastName("first_name");
                person.setId(i);
                session.save(person);
            }

            session.getTransaction().commit();

        }

    }

    @Benchmark
    public void m3() {

        reset();

        for (int i = 0; i < MAX; i++) {
            try (Transaction tx = DATABASE.transactionManager().newTransactionReadWrite()) {
                Person person = Factory.newPerson();
                person.setFirstName("first_name");
                person.setLastName("first_name");
                person.setId(i);
                PERSON_ABSTRACT_REPOSITORY.insert(person);
                tx.commit();
            }
        }
    }

    @Benchmark
    public void m3OneTx() {

        reset();


        try (Transaction tx = DATABASE.transactionManager().newTransactionReadWrite()) {
            for (int i = 0; i < MAX; i++) {

                Person person = Factory.newPerson();
                person.setFirstName("first_name");
                person.setLastName("first_name");
                person.setId(i);
                PERSON_ABSTRACT_REPOSITORY.insert(person);
            }
            tx.commit();
        }

    }

    //@Benchmark
    public void m3OneTxBatch() {
        reset();

        try (Transaction tx = DATABASE.transactionManager().newTransactionReadWrite()) {
            for (int i = 0; i < MAX; i++) {

                Person person = Factory.newPerson();
                person.setFirstName("first_name");
                person.setLastName("first_name");
                person.setId(i);
                //PERSON_ABSTRACT_REPOSITORY.batch(person);
            }

            //PERSON_ABSTRACT_REPOSITORY.executeBatch();

            tx.commit();
        }

    }
}
