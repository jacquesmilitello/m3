package io.m3.sql;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class M3SqlException extends RuntimeException {

    public M3SqlException(String message) {
        super(message);
    }

    public M3SqlException(String message, Exception exception) {
        super(message, exception);
    }

}