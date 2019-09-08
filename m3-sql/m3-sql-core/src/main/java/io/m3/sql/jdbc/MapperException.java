package io.m3.sql.jdbc;

import io.m3.sql.M3SqlException;

import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class MapperException extends M3SqlException {

    private static enum MapperExceptionEnum {
        INSERT, UPDATE, SELECT;
    }

    public MapperException(String sql, InsertMapper<?> m, Object o, SQLException cause) {
        super(buildMessage(MapperExceptionEnum.INSERT, sql, (Mapper<?>) m, o, cause), cause);
    }

    public MapperException(String sql, UpdateMapper<?> m, Object o, SQLException cause) {
        super(buildMessage(MapperExceptionEnum.UPDATE, sql, (Mapper<?>) m, o, cause), cause);
    }

    private static String buildMessage(MapperExceptionEnum type, String sql, Mapper<?> m, Object o, SQLException cause) {
        StringBuilder builder = new StringBuilder();
        builder.append("Failed to set values to InsertMapper :");
        builder.append("\n\ttype    : [").append(type.toString()).append("]");
        builder.append("\n\tsql     : [").append(sql).append("]");
        builder.append("\n\tmapper  : [").append(m).append("]");
        builder.append("\n\tpojo    : [").append(o).append("]");
        builder.append("\n\tcause   : [").append(cause.getMessage()).append("]");
        return builder.toString();
    }
}