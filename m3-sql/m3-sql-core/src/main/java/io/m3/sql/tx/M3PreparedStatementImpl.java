package io.m3.sql.tx;

import io.m3.sql.jdbc.M3PreparedStatement;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

final class M3PreparedStatementImpl implements M3PreparedStatement {

    private final PreparedStatement ps;

    private final TransactionTracer tracer;

    M3PreparedStatementImpl(PreparedStatement ps, TransactionLog tl) {
        this.ps = ps;
        this.tracer = tl.getTransactionTracer();
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        try (TransactionSpan span = tracer.executeQuery(sql)){
            try {
                return this.ps.executeQuery(sql);
            } catch (SQLException cause) {
                span.exception(cause);
                throw cause;
            }
        }
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        try (TransactionSpan span = tracer.executeUpdate(sql)){
            try {
                return this.ps.executeUpdate(sql);
            } catch (SQLException cause) {
                span.exception(cause);
                throw cause;
            }
        }
    }

    @Override
    public void close() throws SQLException {
        try (TransactionSpan span = tracer.sqlClose()){
            try {
                this.ps.close();
            } catch (SQLException cause) {
                span.exception(cause);
                throw cause;
            }
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return this.ps.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        this.ps.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return this.ps.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        this.ps.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        this.ps.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return this.ps.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        this.ps.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        try (TransactionSpan span = tracer.cancel()){
            try {
                this.ps.cancel();
            } catch (SQLException cause) {
                span.exception(cause);
                throw cause;
            }
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.ps.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.ps.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        this.ps.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return this.ps.execute(sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return this.ps.getResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return this.ps.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return this.ps.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        this.ps.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return this.ps.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        this.ps.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return this.ps.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return this.ps.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return this.ps.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        this.ps.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        this.ps.clearBatch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return this.ps.executeBatch();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.ps.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return this.ps.getMoreResults(current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return this.ps.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return this.ps.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return this.ps.executeUpdate(sql, columnIndexes);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return this.ps.executeUpdate(sql, columnNames);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return this.ps.execute(sql, autoGeneratedKeys);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return this.ps.execute(sql, columnIndexes);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return this.ps.execute(sql, columnNames);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return this.ps.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.ps.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        this.ps.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return this.ps.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        this.ps.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return this.ps.isCloseOnCompletion();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.ps.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.ps.isWrapperFor(iface);
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return this.ps.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException {
        return this.ps.executeUpdate();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        //this.tracer.currentSpanCustomizer().annotate("[" + parameterIndex + "] setNull->[" + sqlType + "]");
        this.ps.setNull(parameterIndex, sqlType);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        //this.tracer.currentSpanCustomizer().annotate("[" + parameterIndex + "] setBoolean->[" + x + "]");
        this.ps.setBoolean(parameterIndex, x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        //this.tracer.currentSpanCustomizer().annotate("[" + parameterIndex + "] setByte->[" + x + "]");
        this.ps.setByte(parameterIndex, x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        //this.tracer.currentSpanCustomizer().annotate("setShort(" + parameterIndex + ")->[" + x + "]");
        this.ps.setShort(parameterIndex, x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        //this.tracer.currentSpanCustomizer().annotate("setInt(" + parameterIndex + ")->[" + x + "]");
        this.ps.setInt(parameterIndex, x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        //this.tracer.currentSpanCustomizer().annotate("setLong(" + parameterIndex + ")->[" + x + "]");
        this.ps.setLong(parameterIndex, x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setFloat(" + parameterIndex + ")->[" + x + "]");
        this.ps.setFloat(parameterIndex, x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        // this.tracer.currentSpanCustomizer().annotate("setDouble(" + parameterIndex + ")->[" + x + "]");
        this.ps.setDouble(parameterIndex, x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setBigDecimal(" + parameterIndex + ")->[" + x + "]");
        this.ps.setBigDecimal(parameterIndex, x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        //   this.tracer.currentSpanCustomizer().annotate("setString(" + parameterIndex + ")->[" + x + "]");
        this.ps.setString(parameterIndex, x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        // this.tracer.currentSpanCustomizer().annotate("setBytes(" + parameterIndex + ")");
        this.ps.setBytes(parameterIndex, x);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        // this.tracer.currentSpanCustomizer().annotate("setDate(" + parameterIndex + ")->[" + x + "]");
        this.ps.setDate(parameterIndex, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        // this.tracer.currentSpanCustomizer().annotate("setTime(" + parameterIndex + ")->[" + x + "]");
        this.ps.setTime(parameterIndex, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        //   this.tracer.currentSpanCustomizer().annotate("setTimestamp(" + parameterIndex + ")->[" + x + "]");
        this.ps.setTimestamp(parameterIndex, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setAsciiStream(" + parameterIndex + ")");
        this.ps.setAsciiStream(parameterIndex, x, length);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setUnicodeStream(" + parameterIndex + ")");
        this.ps.setUnicodeStream(parameterIndex, x, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setBinaryStream(" + parameterIndex + ")");
        this.ps.setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void clearParameters() throws SQLException {
        this.ps.clearParameters();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setObject(" + parameterIndex + "," + targetSqlType + ")->[" + x + "]");
        this.ps.setObject(parameterIndex, x);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setObject(" + parameterIndex + ")->[" + x + "]");
        this.ps.setObject(parameterIndex, x);
    }

    @Override
    public boolean execute() throws SQLException {
        return this.ps.execute();
    }

    @Override
    public void addBatch() throws SQLException {
        this.ps.addBatch();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        //   this.tracer.currentSpanCustomizer().annotate("setCharacterStream(" + parameterIndex + ")");
        this.ps.setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        //   this.tracer.currentSpanCustomizer().annotate("setRef(" + parameterIndex + ")");
        this.ps.setRef(parameterIndex, x);
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setBlob(" + parameterIndex + ")");
        this.ps.setBlob(parameterIndex, x);
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        //   this.tracer.currentSpanCustomizer().annotate("setClob(" + parameterIndex + ")");
        this.ps.setClob(parameterIndex, x);
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setArray(" + parameterIndex + ")");
        this.ps.setArray(parameterIndex, x);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.ps.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        // this.tracer.currentSpanCustomizer().annotate("setDate(" + parameterIndex + "," + cal + ")->[" + x + "]");
        this.ps.setDate(parameterIndex, x, cal);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        //   this.tracer.currentSpanCustomizer().annotate("setTime(" + parameterIndex + "," + cal + ")->[" + x + "]");
        this.ps.setTime(parameterIndex, x, cal);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        //   this.tracer.currentSpanCustomizer().annotate("setTimestamp(" + parameterIndex + "," + cal + ")->[" + x + "]");
        this.ps.setTimestamp(parameterIndex, x, cal);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        //this.tracer.currentSpanCustomizer().annotate("setNull(" + parameterIndex + "," + typeName + ")->[" + sqlType + "]");
        this.ps.setNull(parameterIndex, sqlType, typeName);
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setURL(" + parameterIndex + ")->[" + x + "]");
        this.ps.setURL(parameterIndex, x);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return this.ps.getParameterMetaData();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        //   this.tracer.currentSpanCustomizer().annotate("setRowId(" + parameterIndex + ")->[" + x + "]");
        this.ps.setRowId(parameterIndex, x);
    }

    @Override
    public void setNString(int parameterIndex, String x) throws SQLException {
        // this.tracer.currentSpanCustomizer().annotate("setNString(" + parameterIndex + ")->[" + x + "]");
        this.ps.setNString(parameterIndex, x);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        //   this.tracer.currentSpanCustomizer().annotate("setNCharacterStream(" + parameterIndex + ")");
        this.ps.setNCharacterStream(parameterIndex, value, length);
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        // this.tracer.currentSpanCustomizer().annotate("setNClob(" + parameterIndex + ")");
        this.ps.setNClob(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        //this.tracer.currentSpanCustomizer().annotate("setClob(" + parameterIndex + ")");
        this.ps.setClob(parameterIndex, reader, length);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        // this.tracer.currentSpanCustomizer().annotate("setBlob(" + parameterIndex + ")");
        this.ps.setBlob(parameterIndex, inputStream, length);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        //this.tracer.currentSpanCustomizer().annotate("setNClob(" + parameterIndex + ")");
        this.ps.setNClob(parameterIndex, reader, length);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        // this.tracer.currentSpanCustomizer().annotate("setSQLXML(" + parameterIndex + ")");
        this.ps.setSQLXML(parameterIndex, xmlObject);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        // this.tracer.currentSpanCustomizer().annotate("setObject(" + parameterIndex + "," + targetSqlType + "," + scaleOrLength + ")->[" + x + "]");
        this.ps.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        //this.tracer.currentSpanCustomizer().annotate("setAsciiStream(" + parameterIndex + ")");
        this.ps.setAsciiStream(parameterIndex, x, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        // this.tracer.currentSpanCustomizer().annotate("setBinaryStream(" + parameterIndex + ")");
        this.ps.setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setCharacterStream(" + parameterIndex + ")");
        this.ps.setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        //   this.tracer.currentSpanCustomizer().annotate("setAsciiStream(" + parameterIndex + ")");
        this.ps.setAsciiStream(parameterIndex, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setBinaryStream(" + parameterIndex + ")");
        this.ps.setBinaryStream(parameterIndex, x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        //    this.tracer.currentSpanCustomizer().annotate("setCharacterStream(" + parameterIndex + ")");
        this.ps.setCharacterStream(parameterIndex, reader);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setNCharacterStream(" + parameterIndex + ")");
        this.ps.setNCharacterStream(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        // this.tracer.currentSpanCustomizer().annotate("setClob(" + parameterIndex + ")");
        this.ps.setClob(parameterIndex, reader);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        //  this.tracer.currentSpanCustomizer().annotate("setBlob(" + parameterIndex + ")");
        this.ps.setBlob(parameterIndex, inputStream);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        // this.tracer.currentSpanCustomizer().annotate("setNClob(" + parameterIndex + ")");
        this.ps.setNClob(parameterIndex, reader);
    }

    @Override
    public void free() {

    }
}
