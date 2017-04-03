package io.m3.sql.impl;

import io.m3.sql.Database;
import io.m3.sql.Dialect;
import io.m3.sql.Module;
import io.m3.sql.tx.TransactionManager;
import io.m3.sql.tx.TransactionManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseImpl implements Database {

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
     * Modules supported for this instance of datatabase.
     */
    private final Module[] modules;

    /**
     * @param dataSource    the {@link DataSource}
     * @param dialect
     * @param defaultSchema
     */
    public DatabaseImpl(DataSource dataSource, Dialect dialect, String defaultSchema, Module module, Module... modules) {
        this.dataSource = dataSource;
        this.dialect = dialect;
        this.transactionManager = new TransactionManagerImpl(this);

        if (modules != null) {
            this.modules = new Module[]{module};
        } else {
            this.modules = new Module[1 + modules.length];
            this.modules[0] = module;
            System.arraycopy(modules, 0, this.modules, 1, modules.length);
        }
        trace(dataSource, this.modules);
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


    private static void trace(DataSource dataSource, Module[] modules) {

        StringBuilder builder = new StringBuilder();
        builder.append("\n--------------------------------------------------------------------------------");
        try (Connection connection = dataSource.getConnection()) {

            DatabaseMetaData meta = connection.getMetaData();

            builder.append("\nDatabase -> name : [").append(meta.getDatabaseProductName()).append("] -> version : [")
                    .append(meta.getDatabaseProductVersion()).append("] \n         -> Major [").append(meta.getDatabaseMajorVersion())
                    .append("] -> Minor [").append(meta.getDatabaseMinorVersion()).append("]");
            builder.append("\nJDBC     -> name : [").append(meta.getDriverName()).append("] -> version : [")
                    .append(meta.getDriverVersion()).append("]").append("] \n         -> Major [").append(meta.getDriverMajorVersion())
                    .append("] -> Minor [").append(meta.getDriverMinorVersion()).append("]");


            for (Module module : modules) {
                DatabaseSchemaChecker.checkModule(builder, meta, module);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        builder.append("\n--------------------------------------------------------------------------------");

        LOGGER.info("init database :{}", builder.toString());
    }
}
