package io.m3.sql.desc;

/**
 * @author <a href="mailto:jacques.militello@olky.eu">Jacques Militello</a>
 */
public final class SqlPrimaryKey extends SqlColumn {

    public SqlPrimaryKey(SqlTable table, String name) {
        super(table, name, false, true, false);
    }

}