package io.m3.sql.id;

import io.m3.sql.M3SqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class IdentifierException extends M3SqlException {

    public IdentifierException(String message) {
        super(message);
    }

}