package io.m3.sql.jdbc;

import java.sql.PreparedStatement;

public interface M3PreparedStatement extends PreparedStatement, AutoCloseable {

    void close();

}