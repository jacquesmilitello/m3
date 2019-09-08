package io.m3.sql.builder;

import io.m3.sql.M3SqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SelectBuilderException extends M3SqlException{

    public SelectBuilderException(String message) {
        super(message);
    }
}
