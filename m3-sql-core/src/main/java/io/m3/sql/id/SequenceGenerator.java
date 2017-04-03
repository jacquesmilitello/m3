package io.m3.sql.id;


import io.m3.sql.Database;
import io.m3.sql.M3SqlException;
import io.m3.sql.tx.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class SequenceGenerator<T> implements Identifier<T> {


    private final String sequence;


    public SequenceGenerator(String sequence) {
        this.sequence = sequence;
    }

    public final T next(Database database, Transaction transaction) throws M3SqlException {

        String sql = database.dialect().nextVal(this.sequence);

        PreparedStatement ps = transaction.select(sql);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return extractResult(rs);
            } else {
                throw new M3SqlException("NO SEQUENCE");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        //database.dataSource().nextVal()

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
