package io.m3.sql.tx;

import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionTracer  {

    public void exeception(SQLException cause) {
	}

    public TransactionSpan executeQuery(String sql) {
		return null;
	}

	public TransactionSpan executeUpdate(String sql) {
		return null;
	}

	public TransactionSpan sqlClose() {
		return null;
	}

	public TransactionSpan cancel() {
		return null;
	}

}