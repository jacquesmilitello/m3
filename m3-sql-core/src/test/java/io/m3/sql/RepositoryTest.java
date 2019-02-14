package io.m3.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import javax.sql.DataSource;

import io.m3.sql.builder.InsertBuilder;
import io.m3.sql.builder.SelectBuilder;
import io.m3.sql.builder.UpdateBuilder;
import io.m3.sql.jdbc.Mapper;
import io.m3.sql.jdbc.MapperException;
import io.m3.sql.jdbc.PreparedStatementSetter;
import io.m3.sql.jdbc.M3PreparedStatementSetterException;
import io.m3.sql.model.PojoI;
import io.m3.sql.model.Pojos;
import io.m3.sql.tx.M3TransactionException;
import io.m3.sql.tx.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class RepositoryTest {

	private DataSource datasource;
	private Connection conn;
	private PreparedStatement ps;
	private Database db;
	private PreparedStatementSetter pss;
	private ResultSet rs;
	private Mapper<PojoI> map;
	private PojoI pojo;

	@BeforeEach
	void beforeEach() throws SQLException {
		datasource = mock(DataSource.class);
		conn = mock(Connection.class);
		
		when(datasource.getConnection()).thenReturn(conn);
		
		ps = mock(PreparedStatement.class);
		@SuppressWarnings("unchecked")
		Mapper<PojoI> mapTemp = mock(Mapper.class);
		map = mapTemp;
		pojo = mock(PojoI.class);

		rs = mock(ResultSet.class);
		pss = mock(PreparedStatementSetter.class);

		when(conn.getTransactionIsolation()).thenReturn(1);
		db = Pojos.mockDatabase(datasource);
		
		verify(conn).close();
		reset(conn);
	}

	@Test
	void testSelect() {
		Database db = Pojos.DATABASE;
		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		assertEquals("SELECT id,parent_fk,path,full_path,created_at,created_by FROM folder", builder.build());
	}

	@Test
	void testExecuteSelect() throws SQLException {
		when(ps.executeQuery()).thenReturn(rs);
		when(conn.prepareStatement(anyString(),  anyInt())).thenReturn(ps);
		when(rs.next()).thenReturn(true); // has result
		when(map.map(Mockito.any(), Mockito.any())).thenReturn(mock(PojoI.class));

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			PojoI p = repo.executeSelect(builder.build(), pss, map);
			Assertions.assertNotNull(p);
			tx.rollback();
		}
		verify(ps).executeQuery();
		verify(rs).next();
		verify(map).map(db.dialect(), rs);
		verify(conn).close();
	}

	@Test
	void testExecuteSelectFailsOnPrepareStatement() throws SQLException {
		when(conn.prepareStatement(anyString(),  anyInt())).thenThrow(new SQLException());

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			M3RepositoryException ex = assertThrows(M3RepositoryException.class, () -> repo.executeSelect(builder.build(), pss, map));
			assertEquals(M3RepositoryException.Type.PREPARED_STATEMENT, ex.getType());
			tx.rollback();
		}
		verify(conn).close();
	}

	@Test
	void testExecuteSelectFailsOnFillPrepareStatement() throws SQLException {
		doThrow(SQLException.class).when(pss).set(Mockito.any());
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			M3RepositoryException ex = assertThrows(M3RepositoryException.class, () -> repo.executeSelect(builder.build(), pss, map));
			assertEquals(M3RepositoryException.Type.PREPARED_STATEMENT_SETTER, ex.getType());
		}
		verify(conn).close();
		verify(conn).rollback();
	}

	@Test
	void testExecuteSelectFailsOnExecuteQuery() throws SQLException {
		when(rs.next()).thenReturn(true); // has result
		doThrow(SQLException.class).when(ps).executeQuery();
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
		when(map.map(Mockito.any(), Mockito.any())).thenReturn(mock(PojoI.class));

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(M3SqlException.class, () -> repo.executeSelect(builder.build(), pss, map));
			tx.rollback();
		}
		verify(conn).close();
		verify(conn).rollback();
	}

	@Test
	void testExecuteSelectFailsOnResultsetNext() throws SQLException {
		when(ps.executeQuery()).thenReturn(rs);
		doThrow(SQLException.class).when(rs).next();
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
		when(map.map(Mockito.any(), Mockito.any())).thenReturn(mock(PojoI.class));

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(M3SqlException.class, () -> repo.executeSelect(builder.build(), pss, map));
		}
	}

	@Test
	void testExecuteSelectReturnsNoResult() throws SQLException {
		when(ps.executeQuery()).thenReturn(rs);
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
		when(rs.next()).thenReturn(false);

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			PojoI p = repo.executeSelect(builder.build(), pss, map);
			Assertions.assertNull(p);
			tx.rollback();
		}
		verify(ps).executeQuery();
		verify(rs).next();
		verify(conn).close();
	}

	// INSERT

	@Test
	void testExecuteInsert() throws SQLException {
		
		when(ps.executeUpdate()).thenReturn(1);// success
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
		
		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			repo.executeInsert(builder.build(), map, pojo);
			tx.commit();

		}
		verify(ps).executeUpdate();
		verify(conn).close();
	}

	@Test
	void testExecuteInsertFailsOnPrepareStatement() throws SQLException {
		doThrow(SQLException.class).when(conn).prepareStatement(anyString(), anyInt());

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			Assertions.assertThrows(M3RepositoryException.class, () -> repo.executeInsert(builder.build(), map, pojo));
			tx.rollback();
		}

		verify(conn).close();
	}

	@Test
	void testExecuteInsertFailsOnFillPrepareStatement() throws SQLException {
		
		doThrow(SQLException.class).when(map).insert(ps, pojo);
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(M3SqlException.class, () -> repo.executeInsert(builder.build(), map, pojo));
			tx.rollback();
		}

	}

	@Test
	void testExecuteInsertFailsOnExecuteUpdate() throws SQLException {
		
		doThrow(SQLException.class).when(ps).executeUpdate();
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(M3SqlException.class, () -> repo.executeInsert(builder.build(), map, pojo));
			tx.rollback();
		}
	}

	@Test
	void testExecuteInsertFailsOnWrongNumberUpdated() throws SQLException {
		
		when(ps.executeUpdate()).thenReturn(0);// success
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(M3SqlException.class, () -> repo.executeInsert(builder.build(), map, pojo));
			tx.rollback();
		}
	}

	// UPDATE

	@Test
	void testExecuteUpdate() throws SQLException {
		
		when(ps.executeUpdate()).thenReturn(1);// success
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			repo.executeUpdate(builder.build(), map, pojo);
			tx.rollback();
		}
		verify(ps).executeUpdate();
		verify(conn).close();
	}

	@Test
	void testExecuteUpdateFailsOnPrepareStatement() throws SQLException {
		
		doThrow(SQLException.class).when(conn).prepareStatement(anyString(), anyInt());
		when(conn.createStatement()).thenReturn(mock(Statement.class));
		
		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			Assertions.assertThrows(M3TransactionException.class, () -> repo.executeUpdate(builder.build(), map, pojo));
		}
	}

	//@Test
	void testExecuteUpdateFailsOnFillPrepareStatement() throws SQLException {
		
		doThrow(SQLException.class).when(map).update(ps, pojo);
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
		when(conn.createStatement()).thenReturn(mock(Statement.class));
		
		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(MapperException.class, () -> repo.executeUpdate(builder.build(), map, pojo));
		}
	}

	//@Test
	void testExecuteUpdateFailsOnExecuteUpdate() throws SQLException {
		
		doThrow(SQLException.class).when(ps).executeUpdate();
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
		when(conn.createStatement()).thenReturn(mock(Statement.class));
		
		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(M3SqlException.class, () -> repo.executeUpdate(builder.build(), map, pojo));
		}
	}

	//@Test
	void testExecuteUpdateFailsOnWrongNumberUpdated() throws SQLException {
		
		when(ps.executeUpdate()).thenReturn(0);// success
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
		when(conn.createStatement()).thenReturn(mock(Statement.class));
		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(RuntimeException.class, () -> repo.executeUpdate(builder.build(), map, pojo));
		}
	}

	//@Test
	void testInsert() {
		Database db = Pojos.DATABASE;
		DefaultRepository repo = new DefaultRepository(db);
		InsertBuilder builder = repo.insert(Pojos.FOLDER_TABLE, Pojos.FOLDER_IDS, Pojos.FOLDER_COLUMNS);
		assertEquals("INSERT INTO folder (id,parent_fk,path,full_path,created_at) VALUES (?,?,?,?,?)", builder.build());
	}

	//@Test
	void testUpdate() {
		Database db = Pojos.DATABASE;
		DefaultRepository repo = new DefaultRepository(db);
		UpdateBuilder builder = repo.update(Pojos.FOLDER_TABLE, Pojos.FOLDER_COLUMNS, Pojos.FOLDER_IDS);
		assertEquals("UPDATE folder SET parent_fk=?,path=?,full_path=?,created_at=? WHERE id=?", builder.build());
	}

	//@Test
	void testDatabase() {
		Database db = Pojos.DATABASE;
		DefaultRepository repo = new DefaultRepository(db);
		assertEquals(db, repo.database());
	}

	static class DefaultRepository extends Repository {

		DefaultRepository(Database database) {
			super(database);
		}

	}
}
