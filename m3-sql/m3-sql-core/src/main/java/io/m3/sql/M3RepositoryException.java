package io.m3.sql;

import java.sql.SQLException;

import io.m3.sql.M3SqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class M3RepositoryException extends M3SqlException {

	public enum Type {
		PREPARED_STATEMENT_SETTER, INSERT_GENERATED_KEYS, EXECUTE_QUERY, RESULT_SET_NEXT, RESULT_SET_MAPPER
	}

	private final Type type;

	public M3RepositoryException(Type type, SQLException exception) {
		super(exception);
		this.type = type;
	}

	public Type getType() {
		return this.type;
	}

}
