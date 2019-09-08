package io.m3.sql.builder;

import io.m3.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Order {

    private final SqlColumn column;
    private final OrderType type;
    
    private Order(SqlColumn column, OrderType type) {
        this.column = column;
        this.type = type;
    }

    public static Order asc(SqlColumn column) {
        return new Order(column, OrderType.ASC);
    }

    public static Order desc(SqlColumn column) {
        return new Order(column, OrderType.DESC);
    }

    public OrderType type() {
        return this.type;
    }
    
    public SqlColumn column() {
        return this.column;
    }
}
