package io.m3.sql.impl;

import io.m3.sql.Database;
import io.m3.sql.Dialect;
import io.m3.sql.tx.TransactionManager;
import io.m3.sql.tx.TransactionManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Created by jmilitello on 26/03/2017.
 */
public class DatabaseImpl implements Database {


    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

    /**
     * SQL Datasource
     */
    private final DataSource dataSource;

    /**
     * Dialect for the given datasource.
     */
    private final Dialect dialect;


    private final TransactionManager transactionManager;

    /**
     * @param dataSource the {@link DataSource}
     * @param dialect
     * @param defaultSchema
     */
    public DatabaseImpl(DataSource dataSource, Dialect dialect, String defaultSchema) {
        this.dataSource = dataSource;
        this.dialect = dialect;
        this.transactionManager = new TransactionManagerImpl(this);
    }


    public Dialect dialect() {
        return this.dialect;
    }

    public TransactionManager transactionManager() {
        return this.transactionManager;
    }

    public DataSource dataSource() {
        return this.dataSource;
    }

    public boolean isMonoSchema() {
        return true;
    }
}
