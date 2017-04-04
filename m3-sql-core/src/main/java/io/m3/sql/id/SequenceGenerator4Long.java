package io.m3.sql.id;

import io.m3.sql.Database;
import io.m3.sql.M3SqlException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by jmilitello on 03/04/2017.
 */
public final class SequenceGenerator4Long extends SequenceGenerator<Long> {


    public SequenceGenerator4Long(Database database,String sequence) {
        super(database, sequence);
    }

    @Override
    protected Long extractResult(ResultSet rs) throws SQLException {
        return Long.valueOf(rs.getLong(1));
    }
}
