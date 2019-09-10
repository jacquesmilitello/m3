package io.m3.sql.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface InsertMapperWithAutoIncrement<T> extends InsertMapper<T>{

    void setId(T pojo, ResultSet rs) throws SQLException;

}
