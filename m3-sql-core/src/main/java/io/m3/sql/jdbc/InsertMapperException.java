package io.m3.sql.jdbc;

import io.m3.sql.M3SqlException;

import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InsertMapperException extends M3SqlException{

    public InsertMapperException(String sql, InsertMapper<?> im, Object o, SQLException cause) {
        super(buildMessage(sql, im, o, cause), cause);
    }

    private static String buildMessage(String sql, InsertMapper<?> im, Object o, SQLException cause) {
        StringBuilder builder = new StringBuilder();
        builder.append("Failed to set values to InsertMapper :");
        builder.append("\n\tsql     : [").append(sql).append("]");
        builder.append("\n\tmapper  : [").append(im).append("]");
        builder.append("\n\tpojo    : [").append(o).append("]");
        builder.append("\n\tcause   : [").append(cause.getMessage()).append("]");
        return builder.toString();
    }
}