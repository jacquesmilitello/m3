package io.m3.sql.tx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    private final Map<String, PreparedStatement> selects = new HashMap<>();
    private final Map<String, PreparedStatement> inserts = new HashMap<>();
    private final Map<String, PreparedStatement> updates = new HashMap<>();


    protected AbstractTransaction(TransactionManagerImpl transactionManager, Connection connection) {
        this.transactionManager = transactionManager;
        this.connection = connection;
    }

    /** {@inheritDoc} */
    @Override
    public final void close() {

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

    public PreparedStatement select(String sql) {

        PreparedStatement ps = this.selects.get(sql);

        if (ps == null) {
            try {
                ps = connection.prepareStatement(sql);
            } catch (SQLException cause) {
                throw new TransactionException("Failed to prepare statement for SQL [" + sql + "]", cause);
            }
            this.selects.put(sql, ps);
        }

        return ps;

    }

    public PreparedStatement insert(String sql) {

        PreparedStatement ps = this.inserts.get(sql);

        if (ps == null) {
            try {
                ps = connection.prepareStatement(sql);
            } catch (SQLException cause) {
                throw new TransactionException("Failed to prepare statement for SQL [" + sql + "]", cause);
            }
            this.inserts.put(sql, ps);
        }

        return ps;
    }

    public PreparedStatement update(String sql) {

        PreparedStatement ps = this.updates.get(sql);

        if (ps == null) {
            try {
                ps = connection.prepareStatement(sql);
            } catch (SQLException cause) {
                throw new TransactionException("Failed to prepare statement for SQL [" + sql + "]", cause);
            }
            this.updates.put(sql, ps);
        }

        return ps;
    }


    private void closeStatements() {

    }
}
