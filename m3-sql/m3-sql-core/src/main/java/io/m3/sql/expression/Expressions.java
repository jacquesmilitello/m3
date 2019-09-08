package io.m3.sql.expression;

import io.m3.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class Expressions {

    private Expressions() {
    }

    public static Expression eq(SqlColumn column) {
        return new SimpleExpression(column, "=");
    }

    public static Expression and(Expression left, Expression right, Expression... others) {
        return new LogicalExpression("AND", left, right, others);
    }

    public static Expression or(Expression left, Expression right, Expression... others) {
        return new LogicalExpression("OR", left, right, others);
    }
}
