package io.m3.sql.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface InsertMapper<T> {

    void insert(PreparedStatement ps, T pojo) throws SQLException;

}
