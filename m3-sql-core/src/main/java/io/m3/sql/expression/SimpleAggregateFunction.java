package io.m3.sql.expression;

import io.m3.sql.Dialect;
import io.m3.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SimpleAggregateFunction implements AggregateFunction {

    /**
     * Operation for this SQL (min, max, count, avg, sum).
     */
    private final String function;

    /**
     * Name of this column.
     */
    private final SqlColumn column;

    SimpleAggregateFunction(SqlColumn column, String function) {
        this.function = function;
        this.column = column;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String build(Dialect dialect, boolean alias) {
        StringBuilder builder = new StringBuilder(32);

        builder.append(function).append('(');
        if (alias) {
            builder.append(column.table().alias()).append('.');
        }
        builder.append(column.name()).append(')');
        return builder.toString();
    }

}
