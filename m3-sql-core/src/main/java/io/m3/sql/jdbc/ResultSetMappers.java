package io.m3.sql.jdbc;

import io.m3.sql.Dialect;
import io.m3.sql.M3SqlException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by jmilitello on 30/03/2017.
 */
public final class ResultSetMappers {

    public static final ResultSetMapper<Long> SINGLE_LONG = (dialect, rs) -> {
        long value = rs.getLong(1);
        if (rs.next()) {
            throw new ResultSetMapperException("more than one result");
        }
        return value;
    };

    private ResultSetMappers() {
    }


}
