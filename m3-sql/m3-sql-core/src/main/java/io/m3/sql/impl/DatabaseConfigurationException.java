package io.m3.sql.impl;

import io.m3.sql.M3SqlException;

@SuppressWarnings("serial")
public final class DatabaseConfigurationException extends M3SqlException {

    public DatabaseConfigurationException(String message) {
        super(message);
    }

}
