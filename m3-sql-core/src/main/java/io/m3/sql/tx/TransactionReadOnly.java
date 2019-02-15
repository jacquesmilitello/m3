package io.m3.sql.tx;

import io.m3.sql.jdbc.M3PreparedStatement;

import java.sql.Connection;
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
        throw new M3TransactionException(M3TransactionException.Type.READ_ONLY, "ReadOnly transaction -> commit not allowed.");
    }

    @Override
    public Timestamp timestamp() {
        return null;
    }

    @Override
    public M3PreparedStatement write(String sql) {
        throw new M3TransactionException(M3TransactionException.Type.READ_ONLY, "READ ONLY Transaction : write(" + sql + ")");
    }

    @Override
    public Transaction innerTransaction(TransactionDefinition definition) {
        if (!definition.isReadOnly()) {
            throw new M3TransactionException(M3TransactionException.Type.READ_ONLY, "READ ONLY Transaction ..");
        }
        return new TransactionNested(this);
    }

}
