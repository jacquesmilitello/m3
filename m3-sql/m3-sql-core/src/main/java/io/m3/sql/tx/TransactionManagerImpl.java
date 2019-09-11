package io.m3.sql.tx;

import static io.m3.sql.tx.M3TransactionException.Type.CONNECTION_ISOLATION;
import static io.m3.sql.tx.M3TransactionException.Type.CREATE;
import static io.m3.sql.tx.M3TransactionException.Type.NO_CURRENT_TRANSACTION;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.BiFunction;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionManagerImpl implements TransactionManager {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManagerImpl.class);

    private final DataSource dataSource;

    private final int defaultIsolationLevel;

    private boolean enforceReadOnly = false;

    private final BiFunction<PreparedStatement, TransactionLog,PreparedStatement> decorator;

    private static final BiFunction<PreparedStatement, TransactionLog,PreparedStatement> IDENTITY_DECORATOR = (ps,log) -> ps;

    public TransactionManagerImpl(DataSource dataSource) {
        this(dataSource, IDENTITY_DECORATOR);
    }

    public TransactionManagerImpl(DataSource dataSource, BiFunction<PreparedStatement, TransactionLog, PreparedStatement> decorator) {
        this.dataSource = dataSource;
        this.decorator = decorator;
        try (Connection conn = dataSource.getConnection()) {
            this.defaultIsolationLevel = conn.getTransactionIsolation();
        } catch (SQLException cause) {
            throw new M3TransactionException(CONNECTION_ISOLATION, cause);
        }
    }

    public void setEnforceReadOnly(boolean enforceReadOnly) {
        this.enforceReadOnly = enforceReadOnly;
    }

    @Override
    public Transaction newTransactionReadOnly() {
        return getTransaction(new TransactionDefinitionReadOnly());
    }

    @Override
    public Transaction newTransactionReadWrite() {
        return getTransaction(new TransactionDefinitionReadWrite());
    }

    @Override
    public Transaction current() {
        Transaction transaction = TransactionSynchronizationManager.currentTransaction();
        if (transaction == null) {
            throw new M3TransactionException(NO_CURRENT_TRANSACTION);
        }
        return transaction;
    }

    public Transaction getTransaction(TransactionDefinition definition) throws M3TransactionException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getTransaction({})", definition);
        }
        Transaction tx = TransactionSynchronizationManager.currentTransaction();

        if (tx != null) {
            //get TX inside another TX
            return tx.innerTransaction(definition);
        }

        final Connection conn;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
        } catch (SQLException cause) {
            throw new M3TransactionException(CREATE, cause);
        }

        if (definition.isReadOnly()) {
            tx = new TransactionReadOnly(this, conn);
        } else {
            tx = new TransactionReadWrite(this, conn);
        }


       /* if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
            try {
                conn.setTransactionIsolation(definition.getIsolationLevel());
            } catch (SQLException cause) {
                transaction.close();
                throw new TransactionSystemException(
                        "Cannot set specific TransactionIsolationLevel [" + definition.getIsolationLevel() + "]",
                        cause);
            }
            transaction.addHook(() -> {
                try {
                    conn.setTransactionIsolation(defaultIsolationLevel);
                } catch (SQLException cause) {
                    throw new TransactionSystemException("Cannot reset TransactionIsolationLevel", cause);
                }
            });
        }*/
        TransactionSynchronizationManager.setTransaction(tx);
        return tx;
        //return new SqlTransactionStatus(transaction, definition.isReadOnly());
    }

    BiFunction<PreparedStatement, TransactionLog,PreparedStatement> getDecorator() {
        return this.decorator;
    }

    /*
    @Override
    public void commit(TransactionStatus transactionStatus) throws TransactionException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("commit({})", transactionStatus);
        }
        if (!transactionStatus.isNewTransaction()) {
            // It's a txStatus inside another txStatus
            return;
        }
        if (((SqlTransactionStatus) transactionStatus).isReadOnly()) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Commit a read-only -> rollback");
                rollback(transactionStatus);
                return;
            }
        }
        try {
            TransactionImpl tx = this.transactions.get();
            try {
                tx.commit();
            } finally {
                tx.close();
            }
        } finally {
            this.transactions.remove();
        }
    }

    @Override
    public Transaction openNewReadOnly() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setReadOnly(true);
        return getTransaction(def).getTransaction();
    }

    @Override
    public SqlTransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getTransaction({})", definition);
        }
        TransactionImpl tx = transactions.get();
        if (tx != null) {
            // get TX inside another TX
            return new SqlTransactionStatus(tx, definition.isReadOnly(), false);
        }
        final Connection conn;
        try {
            conn = dataSource.getConnection();
            prepareTransactionalConnection(conn, definition);
            conn.setAutoCommit(false);
        } catch (SQLException cause) {
            throw new CannotCreateTransactionException("Error during Datasource.getConnection", cause);
        }
        TransactionImpl transaction = new TransactionImpl(conn, interceptor);
        if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
            try {
                conn.setTransactionIsolation(definition.getIsolationLevel());
            } catch (SQLException cause) {
                transaction.close();
                throw new TransactionSystemException(
                        "Cannot set specific TransactionIsolationLevel [" + definition.getIsolationLevel() + "]",
                        cause);
            }
            transaction.addHook(() -> {
                try {
                    conn.setTransactionIsolation(defaultIsolationLevel);
                } catch (SQLException cause) {
                    throw new TransactionSystemException("Cannot reset TransactionIsolationLevel", cause);
                }
            });
        }
        this.transactions.set(transaction);
        return new SqlTransactionStatus(transaction, definition.isReadOnly());
    }

    @Override
    public void rollback(TransactionStatus transactionStatus) throws TransactionException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("rollback({})", transactionStatus);
        }
        if (!transactionStatus.isNewTransaction()) {
            // a transaction inside another one.
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("rollback({}) -> skip", transactionStatus);
            }
            return;
        }
        try {
            TransactionImpl tx = this.transactions.get();
            try {
                tx.rollback();
            } finally {
                tx.close();
            }
        } finally {
            this.transactions.remove();
        }
    }

    protected void prepareTransactionalConnection(Connection con, TransactionDefinition definition)
            throws SQLException {
        if (enforceReadOnly && definition.isReadOnly()) {
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate("SET TRANSACTION READ ONLY");
            }
        }
    }
    */
}
