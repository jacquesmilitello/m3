package io.m3.sql;

import io.m3.sql.M3SqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class M3RepositoryException extends M3SqlException {

    public enum Type {
        PREPARED_STATEMENT, PREPARED_STATEMENT_SETTER, INSERT_GENERATED_KEYS
    }

    private final Type type;

    public M3RepositoryException(Type type, String message, Exception exception) {
        super(message, exception);
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

}
