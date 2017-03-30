package io.m3.sql.desc;

/**
 * @author <a href="mailto:jacques.militello@olky.eu">Jacques Militello</a>
 */
public final class SqlSingleColumn extends SqlColumn {

    private final boolean nullable;

    private final boolean insertable;

    private final boolean updatable;

    public SqlSingleColumn(SqlTable table, String name, boolean nullable, boolean insertable, boolean updatable) {
        super(table, name);
        this.nullable = nullable;
        this.insertable = insertable;
        this.updatable = updatable;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isInsertable() {
        return insertable;
    }

    public boolean isUpdatable() {
        return updatable;
    }

}