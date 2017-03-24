package io.m3.sql.tx;


import java.sql.SQLException;

public class TransactionException extends RuntimeException {

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, SQLException cause) {
        super(message,cause);
    }
}
