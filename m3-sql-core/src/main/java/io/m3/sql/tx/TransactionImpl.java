package io.m3.sql.tx;

import io.m3.sql.M3RepositoryException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionImpl implements Transaction {

    private final static Logger LOGGER = LoggerFactory.getLogger(TransactionImpl.class);

    private final Connection connection;

    private final List<Runnable> hooks;

    private final TransactionManagerImpl transactionManager;

    private boolean active;

    private final Map<String, PreparedStatement> insertUpdate = new HashMap<>();
    private final Map<String, PreparedStatement> select = new HashMap<>();

    TransactionImpl(TransactionManagerImpl transactionManager, Connection connection) {
        this.transactionManager = transactionManager;
        this.connection = connection;
        this.hooks = new ArrayList<>();
        this.active = true;
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
            this.active = false;
            transactionManager.clear();
        }
    }

    private void checkActive() {
        if (!this.active) {

        }
    }

    @Override
    public void rollback() {
        try {
            this.connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.active = false;
            transactionManager.clear();
        }
    }

    @Override
    public void close() {

        if (active) {
            LOGGER.info("call close() on a active -> rollback");
            rollback();
        }

        try {
            if (!this.connection.isClosed()) {
                this.connection.close();
            } else {
                LOGGER.warn("connection is already closed()");
            }
        } catch (SQLException cause) {
            LOGGER.warn("Failed to close the connection", cause);
        } finally {
            close(this.select);
            close(this.insertUpdate);
        }


    }

    private void close(Map<String, PreparedStatement> map) {
        map.forEach((sql, ps) -> {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
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
        return new M3PreparedStatementImpl(preparedStatement(sql, this.select));
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

    private PreparedStatement preparedStatement(String sql, Map<String, PreparedStatement> cache) {
        PreparedStatement ps = cache.get(sql);
        if (ps == null) {
            try {
                ps = this.connection.prepareStatement(sql, Statement.NO_GENERATED_KEYS);
                cache.put(sql, ps);
            } catch (SQLException cause) {
                throw new M3RepositoryException(M3RepositoryException.Type.PREPARED_STATEMENT,"Failed to prepare statement for SQL [" + sql + "]", cause);
            }
        }
        return ps;
    }
}
