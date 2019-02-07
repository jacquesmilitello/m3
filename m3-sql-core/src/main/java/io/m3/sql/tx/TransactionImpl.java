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
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionImpl implements Transaction {

    private final static Logger LOGGER = LoggerFactory.getLogger(TransactionImpl.class);

    private final Connection connection;

    private final List<Runnable> hooks;

    TransactionImpl(Connection connection) {
        super();
        this.connection = connection;
        this.hooks = new ArrayList<>();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void commit() {
        try {
            this.connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollback() {

    }

    @Override
    public void close() {

    }

    @Override
    public Timestamp timestamp() {
        return null;
    }

    @Override
    public M3PreparedStatement select(String sql) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("select({})", sql);
        }
        try {
            return new M3PreparedStatementImpl(this.connection.prepareStatement(sql));
        } catch (SQLException cause) {
            throw new M3SqlException("Failed to prepare statement for SQL [" + sql + "]", cause);
        }
    }

    @Override
    public M3PreparedStatement insert(String sql) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("INSERT : [{}]", sql);
        }
        try {
            return new M3PreparedStatementImpl(this.connection.prepareStatement(sql));
        } catch (SQLException cause) {
           throw new M3SqlException("Failed to prepare statement for SQL [" + sql + "]", cause);
        }
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
        try {
            return new M3PreparedStatementImpl(this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
        } catch (SQLException cause) {
            throw new M3SqlException("Failed to prepare statement for SQL [" + sql + "]", cause);
        }
    }

    @Override
    public M3PreparedStatement delete(String sql) {
        return null;
    }

    @Override
    public M3PreparedStatement batch(String sql) {
        return null;
    }

    @Override
    public Iterable<PreparedStatement> getBatchs() {
        return null;
    }

    @Override
    public void addHook(Runnable runnable) {

    }

	/*
	public void close() {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("close()");
		}
		try {
			this.connection.close();
		} catch (SQLException cause) {
			//throw new TransactionUsageException("Error when Connection.close()", cause);
		}
	}

	public PreparedStatement select(String sql) {
		try {
			return this.connection.prepareStatement(sql, PreparedStatement.NO_GENERATED_KEYS);
		} catch (SQLException cause) {
			//throw new PrepareStatementException("Error when connection.prepareStatement() for Select", cause);
			return null;
		}
	}

	public PreparedStatement insert(String sql) {
		try {
			this.connection.prepareStatement(sql, PreparedStatement.NO_GENERATED_KEYS);
		} catch (SQLException cause) {
			//throw new PrepareStatementException("Error when connection.prepareStatement() for Insert", cause);
			return null;
		}
	}

	public PreparedStatement update(String sql) {
		try {
			return this.event.prepareStatement(this.connection.prepareStatement(sql, PreparedStatement.NO_GENERATED_KEYS));
		} catch (SQLException cause) {
			throw new PrepareStatementException("Error when connection.prepareStatement() for Update", cause);
		}
	}
	
	@Override
	public PreparedStatement delete(String sql) {
		try {
			return this.event.prepareStatement(this.connection.prepareStatement(sql, PreparedStatement.NO_GENERATED_KEYS));
		} catch (SQLException cause) {
			throw new PrepareStatementException("Error when connection.prepareStatement() for Delte", cause);
		}
	}

	public void rollback() {
		try {
			this.connection.rollback();
		} catch (SQLException cause) {
			this.event.rollbackException(cause);
			throw new TransactionUsageException("Error in connection.rollback()", cause);
		} finally {
			try {
				this.event.rollback();
			} finally {
				shutdown();	
			}
			
		}
	}

	public void commit() {
		try {
			this.connection.commit();
		} catch (SQLException cause) {
			this.event.commitException(cause);
			throw new TransactionUsageException("Error in connection.commit()", cause);
		} finally {
			try {
				this.event.commit();
			} finally {
				shutdown();	
			}
			
		}
	}

	@Override
	public void addHook(Runnable runnable) {
		this.hooks.add(runnable);
	}
	
	private void shutdown() {

		for (Runnable runnable : hooks) {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Execute hook [{}]", runnable);
			}
			try {
				runnable.run();
			} catch (Exception cause) {
				LOGGER.warn("Failed to run Hook [{}] -> skip", runnable);
			}
		}
		
	}
*/

}
