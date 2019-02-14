package io.m3.sql.tx;

import io.m3.sql.M3SqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class M3TransactionException extends M3SqlException {

    public M3TransactionException(String message) {
        super(message);
    }

    public M3TransactionException(String message, Exception exception) {
        super(message, exception);
    }
}
