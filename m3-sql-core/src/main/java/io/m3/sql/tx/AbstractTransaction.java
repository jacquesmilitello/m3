package io.m3.sql.tx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class AbstractTransaction implements Transaction {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransaction.class);

    /**
     * Holder a SQL connection.
     */
    protected final Connection connection;

    private final TransactionManagerImpl transactionManager;

    private final Map<String, PreparedStatement> statements = new HashMap<>();
    private final Map<String, PreparedStatement> batchs = new HashMap<>();

    protected AbstractTransaction(TransactionManagerImpl transactionManager, Connection connection) {
        this.transactionManager = transactionManager;
        this.connection = connection;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {

        if (LOGGER.isTraceEnabled()) {
            LOGGER.debug("close()");
        }

        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeStatements();
            } finally {
                transactionManager.remove();
            }
        }
    }

    public final PreparedStatement select(String sql) {

        PreparedStatement ps = this.statements.get(sql);

        if (ps == null) {
            try {
                ps = connection.prepareStatement(sql);
            } catch (SQLException cause) {
                throw new TransactionException("Failed to prepare statement for SQL [" + sql + "]", cause);
            }
            this.statements.put(sql, ps);
        }

        return ps;

    }

    public final PreparedStatement insert(String sql) {

        PreparedStatement ps = this.statements.get(sql);

        if (ps == null) {
            try {
                ps = connection.prepareStatement(sql);
            } catch (SQLException cause) {
                throw new TransactionException("Failed to prepare statement for SQL [" + sql + "]", cause);
            }
            this.statements.put(sql, ps);
        }

        return ps;
    }

    public final PreparedStatement insertAutoIncrement(String sql) {

        PreparedStatement ps = this.statements.get(sql);

        if (ps == null) {
            try {
                ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } catch (SQLException cause) {
                throw new TransactionException("Failed to prepare statement for SQL [" + sql + "]", cause);
            }
            this.statements.put(sql, ps);
        }

        return ps;
    }

    public final PreparedStatement update(String sql) {

        PreparedStatement ps = this.statements.get(sql);

        if (ps == null) {
            try {
                ps = connection.prepareStatement(sql);
            } catch (SQLException cause) {
                throw new TransactionException("Failed to prepare statement for SQL [" + sql + "]", cause);
            }
            this.statements.put(sql, ps);
        }

        return ps;
    }

    public final PreparedStatement delete(String sql) {

        PreparedStatement ps = this.statements.get(sql);

        if (ps == null) {
            try {
                ps = connection.prepareStatement(sql);
            } catch (SQLException cause) {
                throw new TransactionException("Failed to prepare statement for SQL [" + sql + "]", cause);
            }
            this.statements.put(sql, ps);
        }

        return ps;
    }

    public final PreparedStatement batch(String sql) {

        PreparedStatement ps = this.batchs.get(sql);

        if (ps == null) {
            try {
                ps = connection.prepareStatement(sql);
            } catch (SQLException cause) {
                throw new TransactionException("Failed to prepare statement for SQL [" + sql + "]", cause);
            }
            this.batchs.put(sql, ps);
        }

        return ps;
    }


    @Override
    public Iterable<PreparedStatement> getBatchs() {
        return this.batchs.values();
    }

    private void closeStatements() {

        try {
            closePreparedStatements(this.statements);
        } finally {
            closePreparedStatements(this.batchs);
        }


    }


    private static void closePreparedStatements(Map<String, PreparedStatement> pss) {
        try {
            for (String sql : pss.keySet()) {
                try {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("close select for SQL [{}]", sql);
                    }
                    pss.get(sql).close();
                } catch (SQLException cause) {
                    LOGGER.error("Failed to close PreparedStatement -> skip", cause);
                }
            }
        } finally {
            pss.clear();
        }
    }
}
