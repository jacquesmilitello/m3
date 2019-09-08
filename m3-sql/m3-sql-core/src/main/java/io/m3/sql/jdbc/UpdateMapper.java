package io.m3.sql.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface UpdateMapper<T> {

    void update(PreparedStatement ps, T pojo) throws SQLException;

}
