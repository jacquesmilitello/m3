package io.m3.sql.tx;


import java.sql.PreparedStatement;
import java.sql.Timestamp;

public interface Transaction extends AutoCloseable {

    boolean isReadOnly();

    void commit();

    void rollback();

    void close();

    Timestamp timestamp();

    PreparedStatement select(String sql);

    PreparedStatement insert(String sql);

    PreparedStatement update(String sql);
}
