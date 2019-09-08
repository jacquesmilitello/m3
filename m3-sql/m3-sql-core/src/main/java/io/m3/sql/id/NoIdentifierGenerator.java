package io.m3.sql.id;

import io.m3.sql.M3SqlException;

public final class NoIdentifierGenerator<T> implements Identifier<T> {

    @Override
    public T next() throws M3SqlException {
        return null;
    }

}
