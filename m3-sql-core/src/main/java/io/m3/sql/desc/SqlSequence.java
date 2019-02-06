package io.m3.sql.desc;

import io.m3.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SqlSequence {

    /**
     * Name of this column.
     */
    private final String name;

    public SqlSequence(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public String toSql() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).toString();
    }

}