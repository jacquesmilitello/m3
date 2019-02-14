package io.m3.sql.tx;

import io.m3.sql.M3SqlException;
import io.m3.sql.jdbc.M3PreparedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
    public void commit() {

        checkActive();

        try {
            this.connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //  this.active = false;
            //  transactionManager.clear();
        }
    }

    private void checkActive() {
        /*if (!this.active) {

        }
        */
    }






    @Override
    public Timestamp timestamp() {
        return null;
    }

    @Override
    public M3PreparedStatement insert(String sql) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("INSERT : [{}]", sql);
        }
        return new M3PreparedStatementImpl(preparedStatement(sql, this.insertUpdate));
    }

    @Override
    public M3PreparedStatement insertAutoIncrement(String sql) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("INSERT AutoIncrement : [{}]", sql);
        }
        try {
            return new M3PreparedStatementImpl(this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
        } catch (SQLException cause) {
            throw new M3SqlException("Failed to prepare statement for SQL [" + sql + "]", cause);
        }
    }

    @Override
    public M3PreparedStatement update(String sql) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("update : [{}]", sql);
        }
        return new M3PreparedStatementImpl(preparedStatement(sql, this.insertUpdate));
    }

    @Override
    public M3PreparedStatement delete(String sql) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("delete : [{}]", sql);
        }
        return new M3PreparedStatementImpl(preparedStatement(sql, this.insertUpdate));
    }

    @Override
    public M3PreparedStatement batch(String sql) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("batch : [{}]", sql);
        }
        return new M3PreparedStatementImpl(preparedStatement(sql, this.insertUpdate));
    }

    @Override
    public Iterable<PreparedStatement> getBatchs() {
        return null;
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
