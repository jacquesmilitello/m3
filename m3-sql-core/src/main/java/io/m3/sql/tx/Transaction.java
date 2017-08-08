package io.m3.sql.tx;


import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Iterator;

public interface Transaction extends AutoCloseable {

    boolean isReadOnly();

    void commit();

    void rollback();

    void close();

    Timestamp timestamp();

    PreparedStatement select(String sql);

    PreparedStatement insert(String sql);

    PreparedStatement insertAutoIncrement(String sql);

    PreparedStatement update(String sql);

    PreparedStatement delete(String sql);

    PreparedStatement batch(String sql);

    Iterable<PreparedStatement> getBatchs();
}
