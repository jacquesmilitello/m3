package io.m3.sql.expression;

import io.m3.sql.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class LogicalExpression implements Expression{

    /**
     * SLF4J Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LogicalExpression.class);

    private final String operation;
    private final Expression left;
    private final Expression right;
    private final Expression[] others;


    LogicalExpression(String op, Expression left, Expression right, Expression... others) {
        this.operation = op;
        this.left = left;
        this.right = right;
        this.others = others.clone();
    }

    @Override
    public String build(Dialect dialect, boolean alias) {
        StringBuilder builder = new StringBuilder(512);
        builder.append('(');
        builder.append(this.left.build(dialect, alias));
        builder.append(' ');
        builder.append(this.operation);
        builder.append(' ');
        builder.append(this.right.build(dialect, alias));

        if (this.others.length > 0) {
            for (Expression other : others) {
                builder.append(' ');
                builder.append(this.operation);
                builder.append(' ');
                builder.append(other.build(dialect, alias));
            }
        }
        builder.append(')');

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Fragment LogicalExpression -> [{}]", builder);
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(512);
        builder.append('(');
        builder.append(this.left.toString());
        builder.append(' ');
        builder.append(this.operation);
        builder.append(' ');
        builder.append(this.right.toString());
        if (this.others.length > 0) {
            for (Expression other : others) {
                builder.append(' ');
                builder.append(this.operation);
                builder.append(' ');
                builder.append(other.toString());
            }
        }
        builder.append(')');
        return builder.toString();
    }

}