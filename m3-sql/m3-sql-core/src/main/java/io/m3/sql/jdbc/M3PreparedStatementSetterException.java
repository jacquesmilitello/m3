package io.m3.sql.jdbc;

import io.m3.sql.M3SqlException;

import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class M3PreparedStatementSetterException extends M3SqlException{

    public M3PreparedStatementSetterException(String sql, PreparedStatementSetter pss, SQLException cause) {
        super(buildMessage(sql, pss, cause), cause);
    }

    private static String buildMessage(String sql, PreparedStatementSetter pss, SQLException cause) {
        StringBuilder builder = new StringBuilder();
        builder.append("Failed to set values to PreparedStatementSetter :");
        builder.append("\n\tsql     : [").append(sql).append("]");
        builder.append("\n\tsetter  : [").append(pss).append("]");
        builder.append("\n\tcause   : [").append(cause.getMessage()).append("]");
        return builder.toString();
    }
}