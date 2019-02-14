package io.m3.sql.impl;

import com.google.common.collect.ImmutableMap;
import io.m3.sql.Database;
import io.m3.sql.Dialect;
import io.m3.sql.Module;
import io.m3.sql.desc.SqlTable;
import io.m3.sql.dialect.Dialects;
import io.m3.sql.tx.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseImpl.class);

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

    private final ImmutableMap<SqlTable, Module> tables;

    /**
     * @param dataSource    the {@link DataSource}
     * @param dialect
     * @param defaultSchema
     */
    public DatabaseImpl(DataSource dataSource, Dialect.Name dialect, TransactionManager transactionManager, String defaultSchema, Module module, Module... modules) {
        this.dataSource = dataSource;
        this.dialect = Dialects.dialect(dialect, this);
        this.transactionManager = transactionManager;

        if (module == null) {
            throw new DatabaseConfigurationException("module is null");
        }
        this.modules = new Module[1 + modules.length];
        this.modules[0] = module;
        if (modules.length > 0) {
            System.arraycopy(modules, 0, this.modules, 1, modules.length);
        }

        ImmutableMap.Builder<SqlTable, Module> builder = ImmutableMap.builder();
        for (Module m : this.modules) {
            m.descriptors().forEach(d -> builder.put(d.table(), m));
        }
        tables = builder.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dialect dialect() {
        return this.dialect;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionManager transactionManager() {
        return this.transactionManager;
    }

    public DataSource dataSource() {
        return this.dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMonoSchema() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Module getModule(SqlTable table) {
        return this.tables.get(table);
    }

    @PostConstruct
    public void postInit() {
        trace(dataSource, this.modules);
    }

    private static void trace(DataSource dataSource, Module[] modules) {

        StringBuilder builder = new StringBuilder();
        builder.append("\n--------------------------------------------------------------------------------");
        try (Connection connection = dataSource.getConnection()) {

            DatabaseMetaData meta = connection.getMetaData();

            // @formatter:off
            builder.append("\nDatabase -> name : [").append(meta.getDatabaseProductName()).append("] -> version : [")
                    .append(meta.getDatabaseProductVersion()).append("] \n         -> Major [")
                    .append(meta.getDatabaseMajorVersion()).append("] -> Minor [")
                    .append(meta.getDatabaseMinorVersion()).append("]");
            builder.append("\nJDBC     -> name : [").append(meta.getDriverName()).append("] -> version : [")
                    .append(meta.getDriverVersion()).append("]").append("] \n         -> Major [")
                    .append(meta.getDriverMajorVersion()).append("] -> Minor [").append(meta.getDriverMinorVersion())
                    .append("]");

            // @formatter:on
            for (Module module : modules) {
                DatabaseSchemaChecker.checkModule(builder, meta, module);
            }

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        builder.append("\n--------------------------------------------------------------------------------");

        LOGGER.info("init database :{}", builder.toString());
    }

}
