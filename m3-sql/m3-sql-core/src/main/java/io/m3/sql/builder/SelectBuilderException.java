package io.m3.sql.builder;

import io.m3.sql.M3SqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class SelectBuilderException extends M3SqlException{

    public SelectBuilderException(String message) {
        super(message);
    }
}
