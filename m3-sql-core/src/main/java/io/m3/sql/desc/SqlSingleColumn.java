package io.m3.sql.desc;

/**
 * @author <a href="mailto:jacques.militello@olky.eu">Jacques Militello</a>
 */
public final class SqlSingleColumn extends SqlColumn {

    public SqlSingleColumn(SqlTable table, String name, boolean nullable, boolean insertable, boolean updatable) {
        super(table, name, nullable, insertable, updatable);
    }

}