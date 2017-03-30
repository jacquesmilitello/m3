package io.m3.sql.desc;

import io.m3.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class SqlColumn {

    /**
     * Table of this column.
     */
    private final SqlTable table;

    /**
     * Name of this column.
     */
    private final String name;

    private final boolean nullable;

    private final boolean insertable;

    private final boolean updatable;

    public SqlColumn(SqlTable table, String name, boolean nullable, boolean insertable, boolean updatable) {
        this.table = table;
        this.name = name;
        this.nullable = nullable;
        this.insertable = insertable;
        this.updatable = updatable;
    }

    public String name() {
        return this.name;
    }

    public SqlTable table() {
        return this.table;
    }

    public String toSql() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("table", table).toString();
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