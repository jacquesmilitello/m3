package io.m3.sql.tx;

import io.m3.sql.jdbc.M3PreparedStatement;
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
final class TransactionReadWrite extends AbstractTransaction {

    private final static Logger LOGGER = LoggerFactory.getLogger(TransactionReadWrite.class);

    private final Map<String, PreparedStatement> insertUpdate = new HashMap<>();

    TransactionReadWrite(TransactionManagerImpl transactionManager, Connection connection) {
        super(transactionManager, connection);
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    protected void doCommit() throws SQLException {
        getConnection().commit();
    }

    @Override
    public PreparedStatement write(String sql) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("WRITE : [{}]", sql);
        }
        return preparedStatement(sql, this.insertUpdate);
    }

  /*  @Override
    public M3PreparedStatement insertAutoIncrement(String sql) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("INSERT AutoIncrement : [{}]", sql);
        }
        try {
            return new M3PreparedStatementImpl(this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
        } catch (SQLException cause) {
            throw new M3SqlException("Failed to prepare statement for SQL [" + sql + "]", cause);
        }
    }*/


    @Override
    public Transaction innerTransaction(TransactionDefinition definition) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("innerTransaction : [{}]", definition);
        }
        return new TransactionNested(this);
    }


    private void shutdown() {

       /* for (Runnable runnable : hooks) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Execute hook [{}]", runnable);
            }
            try {
                runnable.run();
            } catch (Exception cause) {
                LOGGER.warn("Failed to run Hook [{}] -> skip", runnable);
            }
        }*/

    }

}
