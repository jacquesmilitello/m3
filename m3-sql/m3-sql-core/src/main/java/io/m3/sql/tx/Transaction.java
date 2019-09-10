package io.m3.sql.tx;


import java.sql.PreparedStatement;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Transaction extends AutoCloseable {

    boolean isReadOnly();

    void commit();

    void rollback();

    void close();

    PreparedStatement read(String sql);

    PreparedStatement write(String sql);

    void addHook(Runnable runnable);

    Transaction innerTransaction(TransactionDefinition definition);
}
