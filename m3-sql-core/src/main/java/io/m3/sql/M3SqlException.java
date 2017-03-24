package io.m3.sql;

/**
 * Created by jmilitello on 24/03/2017.
 */
public class M3SqlException extends RuntimeException {

    public M3SqlException(String message) {
        super(message);
    }

    public M3SqlException(String message, Exception exception) {
        super(message, exception);
    }

}