package io.m3.sql.expression;

import io.m3.sql.desc.SqlColumn;

/**
 * Created by jmilitello on 23/03/2017.
 */
public class Expressions {

    private Expressions() {
    }

    public static Expression eq(SqlColumn column) {
        return new SimpleExpression(column, "=");
    }

}
