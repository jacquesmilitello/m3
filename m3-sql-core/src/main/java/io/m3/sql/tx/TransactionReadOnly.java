package io.m3.sql.tx;

import io.m3.sql.jdbc.M3PreparedStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionReadOnly extends AbstractTransaction {

    TransactionReadOnly(TransactionManagerImpl transactionManager, Connection connection) {
        super(transactionManager, connection);
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public void commit() {
        throw new RuntimeException("ReadOnly transaction -> commit not allowed.");
    }

    @Override
    public Timestamp timestamp() {
        throw new M3TransactionException("no timestamp -> READ ONLY Transaction");
    }

    @Override
    public M3PreparedStatement insert(String sql) {
        throw new M3TransactionException("READ ONLY Transaction : insert(" + sql + ")");
    }

    @Override
    public M3PreparedStatement insertAutoIncrement(String sql) {
        throw new M3TransactionException("READ ONLY Transaction : insertAutoIncrement(" + sql + ")");
    }

    @Override
    public M3PreparedStatement update(String sql) {
        throw new M3TransactionException("READ ONLY Transaction : update(" + sql + ")");
    }

    @Override
    public M3PreparedStatement delete(String sql) {
        throw new M3TransactionException("READ ONLY Transaction : delete(" + sql + ")");
    }

    @Override
    public M3PreparedStatement batch(String sql) {
        throw new M3TransactionException("READ ONLY Transaction : batch(" + sql + ")");
    }

    @Override
    public Iterable<PreparedStatement> getBatchs() {
        throw new M3TransactionException("READ ONLY Transaction : getBatchs()");
    }


}
