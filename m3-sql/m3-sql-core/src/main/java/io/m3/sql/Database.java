package io.m3.sql;

import io.m3.sql.desc.SqlTable;
import io.m3.sql.tx.TransactionManager;

import javax.sql.DataSource;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Database {

    Dialect dialect();

    TransactionManager transactionManager();

    DataSource dataSource();

    boolean isMonoSchema();

    Module getModule(SqlTable table);
}