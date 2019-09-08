package io.m3.sql.srping;

import io.m3.sql.tx.Transaction;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class M3TransactionStatus implements TransactionStatus {

    public M3TransactionStatus(Transaction current, boolean readOnly, boolean b) {

    }

    @Override
    public boolean isNewTransaction() {
        return false;
    }

    @Override
    public boolean hasSavepoint() {
        return false;
    }

    @Override
    public void setRollbackOnly() {
    }

    @Override
    public boolean isRollbackOnly() {
        return false;
    }

    @Override
    public void flush() {
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public Object createSavepoint() throws TransactionException {
        return null;
    }

    @Override
    public void rollbackToSavepoint(Object o) throws TransactionException {
    }

    @Override
    public void releaseSavepoint(Object o) throws TransactionException {
    }

}
