package io.m3.sql.jdbc;

import io.m3.sql.M3SqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class ResultSetMapperException extends M3SqlException {

    public ResultSetMapperException(String message) {
        super(message);
    }

    public ResultSetMapperException(String message, Exception exception) {
        super(message, exception);
    }


}
