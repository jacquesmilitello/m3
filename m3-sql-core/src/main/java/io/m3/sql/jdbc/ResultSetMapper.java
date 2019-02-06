package io.m3.sql.jdbc;

import io.m3.sql.Dialect;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface ResultSetMapper<T> {

    T map(Dialect dialect, ResultSet rs) throws SQLException;

}
