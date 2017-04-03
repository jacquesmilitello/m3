package io.m3.sql.id;

import io.m3.sql.Database;
import io.m3.sql.M3SqlException;
import io.m3.sql.tx.Transaction;

public final class NoIdentifierGenerator<T> implements Identifier<T> {

    @Override
    public T next(Database database, Transaction transaction) throws M3SqlException {
        return null;
    }

}
