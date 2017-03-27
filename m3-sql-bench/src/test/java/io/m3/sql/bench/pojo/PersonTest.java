package io.m3.sql.bench.pojo;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.junit.Test;

import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Properties;


public class PersonTest {

    @Test
    public void testHibernate() throws Exception {

        JdbcConnectionPool ds = JdbcConnectionPool.create("jdbc:h2:mem:bench;DB_CLOSE_DELAY=-1", "sa", "");

        RunScript.execute(ds.getConnection(), new InputStreamReader(PersonTest.class.getResourceAsStream("/bench.sql")));

        StandardServiceRegistryBuilder serviceRegistryBuilder =  new StandardServiceRegistryBuilder();
        serviceRegistryBuilder.applySetting(Environment.DATASOURCE, ds);

        SessionFactory sessionFactory = new Configuration()
                .addAnnotatedClass(PersonJPA.class)
                .buildSessionFactory(serviceRegistryBuilder.build());


        PersonJPA person = new PersonJPA();
        person.setId(1);
        person.setFirstName("first_name");
        person.setLastName("first_name");

        try (Session session = sessionFactory.openSession()) {

            session.beginTransaction();
            session.save(person);
            session.getTransaction().commit();

        }
    }

}
