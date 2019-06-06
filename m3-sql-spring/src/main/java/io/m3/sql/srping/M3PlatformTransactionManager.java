package io.m3.sql.srping;

import io.m3.sql.tx.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class M3PlatformTransactionManager implements PlatformTransactionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(M3PlatformTransactionManager.class);

    private final TransactionManager transactionManager;

    public M3PlatformTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getTransaction({})", transactionDefinition);
        }

        if (transactionManager.hasCurrent()) {
            // get TX inside another TX
            return new M3TransactionStatus(transactionManager.current(), transactionDefinition.isReadOnly(), false);
        }

        if (transactionDefinition.isReadOnly()) {
            return new M3TransactionStatus(transactionManager.newTransactionReadOnly(), true, true);
        } else {
            return new M3TransactionStatus(transactionManager.newTransactionReadWrite(), false, true);
        }

    }

    @Override
    public void commit(TransactionStatus transactionStatus) throws TransactionException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("commit({})", transactionStatus);
        }



    }

    @Override
    public void rollback(TransactionStatus transactionStatus) throws TransactionException {

    }
}
