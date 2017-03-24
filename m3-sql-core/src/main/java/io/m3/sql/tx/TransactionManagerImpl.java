package io.m3.sql.tx;

import io.m3.sql.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionManagerImpl implements TransactionManager{

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManagerImpl.class);

    private final ThreadLocal<Transaction> transactions = new ThreadLocal<>();

    private final DataSource dataSource;

    public TransactionManagerImpl(Database database) {
        this.dataSource = database.dataSource();
    }

    @Override
    public Transaction current() {

        Transaction tx = this.transactions.get();

        if (tx == null) {
            throw new TransactionException("no current Transaction -> call newTransactionReadOnly() or call newTransactionReadWrite()");
        }

        return tx;
    }

    @Override
    public Transaction newTransactionReadOnly() {

        if (transactions.get() != null) {
            throw new TransactionException("newTransactionReadOnly() -> a transaction already exist [" + transactions.get() + "]");
        }

        Connection connection;
        try {
            connection = this.dataSource.getConnection();
        } catch (SQLException cause) {
            throw new TransactionException("newTransactionReadOnly() -> failed to get a new connection", cause);
        }

        Transaction tx;
        try {
           tx = new TransactionReadOnly(this, connection);
        } catch (SQLException cause) {
            throw new TransactionException("newTransactionReadOnly() -> failed to create a new TransactionReadOnly", cause);
        }

        this.transactions.set(tx);

        return tx;
    }

    @Override
    public Transaction newTransactionReadWrite() {

        if (transactions.get() != null) {
            throw new TransactionException("newTransactionReadWrite() -> a transaction already exist [" + transactions.get() + "]");
        }

        Connection connection;
        try {
            connection = this.dataSource.getConnection();
        } catch (SQLException cause) {
            throw new TransactionException("newTransactionReadWrite() -> failed to get a new connection", cause);
        }

        Transaction tx;
        try {
            tx = new TransactionReadWrite(this, connection);
        } catch (SQLException cause) {
            throw new TransactionException("newTransactionReadWrite() -> failed to create a new TransactionReadWrite", cause);
        }

        this.transactions.set(tx);

        return tx;
    }


    public void remove() {
        this.transactions.remove();
    }
}
