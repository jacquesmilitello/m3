package io.m3.sql.tx;


import io.m3.sql.jdbc.M3PreparedStatement;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Iterator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Transaction extends AutoCloseable {

    boolean isReadOnly();

    void commit();

    void rollback();

    void close();

    Timestamp timestamp();

    M3PreparedStatement select(String sql);

    M3PreparedStatement insert(String sql);

    M3PreparedStatement insertAutoIncrement(String sql);

    M3PreparedStatement update(String sql);

    M3PreparedStatement delete(String sql);

    M3PreparedStatement batch(String sql);

    Iterable<PreparedStatement> getBatchs();

    void addHook(Runnable runnable);

}
