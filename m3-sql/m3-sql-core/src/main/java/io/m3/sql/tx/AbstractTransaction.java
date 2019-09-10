package io.m3.sql.tx;

import io.m3.sql.M3RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractTransaction implements Transaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransaction.class);

    private final TransactionManagerImpl transactionManager;

    private final Connection connection;

    private boolean active;

    private final Map<String, PreparedStatement> select = new HashMap<>();

    private final List<Runnable> hooks;

    private final TransactionLog transactionLog;

    private final BiFunction<PreparedStatement, TransactionLog,PreparedStatement> decorator;

    protected AbstractTransaction(TransactionManagerImpl transactionManager, Connection connection) {
        this.transactionManager = transactionManager;
        this.connection = connection;
        this.active = true;
        this.hooks = new ArrayList<>();
        this.transactionLog = new TransactionLog(this);
        this.decorator = transactionManager.getDecorator();
    }

    @Override
    public final void close() {

        M3TransactionException exception = null;
        try (TransactionSpan span = this.transactionLog.close()) {

            if (active) {
                LOGGER.info("call close() on a active transaction -> rollback");
                try {
                    rollback();
                } catch (M3TransactionException ex) {
                    exception = ex;
                }
            }

            try {
                if (!this.connection.isClosed()) {
                    this.connection.close();
                } else {
                    LOGGER.warn("connection is already closed()");
                }
            } catch (SQLException cause) {
                span.exception(cause);
                LOGGER.warn("Failed to close the connection", cause);
            } finally {
                close(this.select);
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public PreparedStatement read(String sql) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("select({})", sql);
        }
        return preparedStatement(sql, this.select);
    }

    @Override
    public final void rollback() {
        try (TransactionSpan span = this.transactionLog.rollback()) {
            checkActive();

            try {
                this.connection.rollback();
            } catch (SQLException cause) {
                span.exception(cause);
                throw new M3TransactionException(M3TransactionException.Type.ROLLBACK, cause);
            } finally {
                this.active = false;
                transactionManager.clear();
            }
        }
    }

     @Override
    public final void commit() {
        try (TransactionSpan span = this.transactionLog.commit()) {
            checkActive();
            try {
                doCommit();
            } catch (SQLException cause) {
                span.exception(cause);
                throw new M3TransactionException(M3TransactionException.Type.COMMIT, cause);
            } finally {
                this.active = false;
                transactionManager.clear();
            }
        }
    }

    protected abstract void doCommit() throws SQLException;

    @Override
    public final void addHook(Runnable runnable) {
        this.hooks.add(runnable);
    }

    protected final Connection getConnection() {
        return connection;
    }

    protected final TransactionManagerImpl getTransactionManager() {
        return transactionManager;
    }

    protected final void close(Map<String, PreparedStatement> map) {
        try {
            map.forEach((sql, ps) -> {
                try {
                    ps.close();
                } catch (SQLException cause) {
                    LOGGER.warn("Failed to close PreparedStatement -> skip", cause);
                }
            });
        } finally {
            map.clear();
        }
    }

    protected final PreparedStatement preparedStatement(String sql, Map<String, PreparedStatement> cache) {
        PreparedStatement ps = cache.get(sql);
        if (ps == null) {
            try {
                ps = this.decorator.apply(this.connection.prepareStatement(sql, Statement.NO_GENERATED_KEYS), this.transactionLog);
                cache.put(sql, ps);
            } catch (SQLException cause) {
                throw new M3RepositoryException(M3RepositoryException.Type.PREPARED_STATEMENT,"Failed to prepare statement for SQL [" + sql + "]", cause);
            }
        }
        return ps;
    }

    protected final void checkActive() {
        if (!this.active) {
            throw new M3TransactionException(M3TransactionException.Type.NOT_ACTIVE, "");
        }
    }

    protected final void deactivate() {
        this.active = false;
    }

}