package io.m3.sql.tx;

import io.m3.sql.M3SqlException;

import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class M3TransactionException extends M3SqlException {



    public enum Type {
        READ_ONLY, CONNECTION_ISOLATION, NOT_ACTIVE, COMMIT
    }

    private final Type type;

    M3TransactionException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public M3TransactionException(Type type, String message, SQLException cause) {
        super( message, cause);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

}