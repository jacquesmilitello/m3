package io.m3.sql.id;

import io.m3.sql.Database;
import io.m3.sql.M3SqlException;
import io.m3.sql.tx.Transaction;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Identifier<T> {

    T next(Database database, Transaction transaction) throws M3SqlException;
}
