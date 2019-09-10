package io.m3.sql.tx;

import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionSpan implements AutoCloseable {

	@Override
	public void close() {


	}

	public void exception(SQLException cause) {
	}

}
