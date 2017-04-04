package io.m3.sql.id;


import io.m3.sql.Database;
import io.m3.sql.M3SqlException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class SequenceGenerator<T> implements Identifier<T> {


    private final Database database;

    private final String sequence;


    public SequenceGenerator(Database database, String sequence) {
        this.database = database;
        this.sequence = database.dialect().nextVal(sequence);
    }

    public final T next() throws M3SqlException {

        PreparedStatement ps = database.transactionManager().current().select(this.sequence);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return extractResult(rs);
            } else {
                throw new M3SqlException("NO SEQUENCE");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected abstract T extractResult(ResultSet rs) throws SQLException;

//    DB2, Ingres, Oracle
//
//"SCHEMA"."SEQUENCE".nextval
//
//    Sybase SQL Anywhere
//
//[SCHEMA].[SEQUENCE].nextval
//
//            H2, PostgreSQL
//
//    nextval('SCHEMA.SEQUENCE')
//
//    Derby, Firebird, HSQLDB
//
//    next value for "SCHEMA"."SEQUENCE"
//
//    SQL Server
//
//    next value for [SCHEMA].[SEQUENCE]
//
//    CUBRID
//
//"SEQUENCE".next_value

}
