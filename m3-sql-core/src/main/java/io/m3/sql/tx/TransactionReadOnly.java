package io.m3.sql.tx;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionReadOnly extends AbstractTransaction {

	TransactionReadOnly(TransactionManagerImpl transactionManager, Connection connection) throws SQLException {
		super(transactionManager, connection);
		connection.setAutoCommit(false);
		connection.setReadOnly(true);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isReadOnly() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public void commit() {
		throw new RuntimeException("ReadOnly transaction -> commit not allowed.");
	}

	/** {@inheritDoc} */
	@Override
	public void rollback() {
		try {
			this.connection.rollback();
		} catch (SQLException cause) {
			new TransactionException("r", cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Timestamp timestamp() {
		throw new TransactionException("no timestamp -> READ ONLY Transaction");
	}

	@Override
	public void addHook(Runnable runnable) {

	}

}
