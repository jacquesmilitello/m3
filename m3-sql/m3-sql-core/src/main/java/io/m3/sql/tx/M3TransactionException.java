package io.m3.sql.tx;

import io.m3.sql.M3SqlException;

import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class M3TransactionException extends M3SqlException {

    /**
	 *  Generated seruql UID.
	 */
	private static final long serialVersionUID = -7353728745195001854L;

	public enum Type {
		PREPARED_STATEMENT, CREATE, NO_CURRENT_TRANSACTION,  READ_ONLY, CONNECTION_ISOLATION, NOT_ACTIVE, COMMIT , ROLLBACK
    }

    private final Type type;

    M3TransactionException(Type type, AbstractTransaction transaction, TransactionSpan span) {
        super("");
        this.type = type;
    }

    M3TransactionException(Type type, AbstractTransaction transaction, TransactionSpan span, SQLException cause) {
        super(cause);
        this.type = type;
    }

    
    M3TransactionException(Type type) {
        super("");
        this.type = type;
    }

     M3TransactionException(Type type, SQLException cause) {
        super(cause);
        this.type = type;
    }

	public Type getType() {
        return type;
    }

}