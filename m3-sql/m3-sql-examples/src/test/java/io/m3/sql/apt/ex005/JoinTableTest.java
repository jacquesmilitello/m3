package io.m3.sql.apt.ex005;

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

class JoinTableTest {

	private JdbcConnectionPool ds;
	private Database database;

	@BeforeEach
	void before() throws Exception {
		ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
		database = new DatabaseImpl(ds, Dialect.Name.H2, new TransactionManagerImpl(ds), "", new io.m3.sql.apt.ex005.Module("ex005", ""));
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
		
		Role r1 = Factory.newRole();
		r1.setId(1);
		r1.setName("ROLE_ADMIN");
		
		Role r2 = Factory.newRole();
		r2.setId(2);
		r2.setName("ROLE_USER");
		
		Resource res1 = Factory.newResource();
		res1.setId(1);
		res1.setName("/api/1.0/{id}");
		
		AbstractRoleRepository repository = new AbstractRoleRepository(database) {
        };
        
        AbstractResourceRepository resourceRepository = new AbstractResourceRepository(database) {
        };

        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
        	repository.insert(r1);
        	repository.insert(r2);
        	resourceRepository.insert(res1);
        	
        	// Associate.link(r1, res1);
        	// Associate.unlink(r1, res1);
        	
        	// Associate.list(res1Id) -> list Role
        	// Associate.list(roleId) -> list Resource
        	
        	//  Associate.list(res1Id) by BK
        	
        	tx.commit();
        }
        
        

	}
}
