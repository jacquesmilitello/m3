package io.m3.sql.desc;

import com.google.common.hash.HashCode;
import io.m3.util.ToStringBuilder;

import java.util.Objects;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SqlTable {

    /**
     * Name of this table.
     */
    private final String name;

    private final String alias;

    public SqlTable(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String name() {
        return this.name;
    }

    public String alias() {
        return this.alias;
    }

    /**

 * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("alias", alias)
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof  SqlTable)) {
            return false;
        }

        SqlTable that = (SqlTable) o;

        return Objects.equals(name(), that.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
       return this.name.hashCode();
    }
}