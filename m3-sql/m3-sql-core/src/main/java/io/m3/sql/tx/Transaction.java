package io.m3.sql.tx;


import java.sql.Timestamp;

import io.m3.sql.jdbc.M3PreparedStatement;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Transaction extends AutoCloseable {

    boolean isReadOnly();

    void commit();

    void rollback();

    void close();

    Timestamp timestamp();

    M3PreparedStatement read(String sql);

    M3PreparedStatement write(String sql);

    void addHook(Runnable runnable);

    Transaction innerTransaction(TransactionDefinition definition);
}
