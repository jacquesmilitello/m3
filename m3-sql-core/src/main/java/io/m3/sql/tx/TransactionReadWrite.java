package io.m3.sql.tx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionReadWrite extends AbstractTransaction {

	/**
	 * SLF4J Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionReadWrite.class);

	private boolean active = true;

	TransactionReadWrite(TransactionManagerImpl transactionManager, Connection connection) throws SQLException {
		super(transactionManager, connection);
		connection.setAutoCommit(false);
		connection.setReadOnly(false);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isReadOnly() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public void commit() {
		active = false;
		try {
			this.connection.commit();
		} catch (SQLException cause) {
			throw new TransactionException("failed to commit", cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void rollback() {
		active = false;
		try {
			this.connection.rollback();
		} catch (SQLException cause) {
			throw new TransactionException("r", cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Timestamp timestamp() {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public void close() {

		try {
			if (active) {
				LOGGER.warn("close() an active ReadWrite Transaction (no commit() or rollback() called)");
			}

		} finally {
			super.close();
		}
	}
}
