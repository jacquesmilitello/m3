package io.m3.sql.builder;

import io.m3.sql.Database;
import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlTable;
import io.m3.sql.expression.Expression;
import io.m3.util.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.m3.util.ImmutableList.of;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SelectBuilder extends AbstractBuilder {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SelectBuilder.class);

    private final ImmutableList<SqlColumn> columns;
    private Expression where;
    private ImmutableList<SqlTable> from;
    private final List<JoinClause> joins = new ArrayList<>();
    private final List<Order> orderBy = new ArrayList<>();
    private boolean forUpdate = false;
    private int limit = -1;

    public SelectBuilder(Database database, ImmutableList<SqlColumn> columns) {
        super(database);
        this.columns = columns;
    }

    public String build() {

        validate();

        StringBuilder builder = new StringBuilder(2048);
        builder.append("SELECT ");
        appendSelect(builder);

        appendFrom(builder);

        appendJoins(builder);
        appendWhere(builder);
        appendOrder(builder);

        if (forUpdate) {
            builder.append(" FOR UPDATE");
        }

        if (limit != -1) {
            builder.append(" LIMIT ").append(this.limit);
        }

        String sql = builder.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL [{}]", sql);
        }
        return sql;
    }

    public SelectBuilder forUpdate() {
        this.forUpdate = true;
        return this;
    }

    public SelectBuilder from(SqlTable ... tables) {
        this.from = of(tables);
        return this;
    }

    public SelectBuilder orderBy(Order order) {
        this.orderBy.add(order);
        return this;
    }

    public SelectBuilder where(Expression expression) {
        this.where = expression;
        return this;
    }

    public SelectBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public SelectBuilder leftJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
        requireNonNull(targetTable, "leftJoin() -> targetTable should be not null");
        requireNonNull(targetColumn, "leftJoin() -> targetColumn should be not null");
        requireNonNull(targetColumn, "leftJoin() -> column should be not null");

        if (this.from == null) {
            throw new SelectBuilderException("leftJoin() -> call from() before this.");
        }

        if (!this.from.contains(column.table())) {
            throw new SelectBuilderException("leftJoin() -> join column [" + column + "] not found in from clause.");
        }

        this.joins.add(new JoinClause(this.database(), JoinType.LEFT, targetTable, targetColumn, column.table(), column));
        return this;
    }

    public SelectBuilder leftJoin(SqlTable targetTable, SqlColumn targetColumn, SqlTable from, SqlColumn column) {
        requireNonNull(targetTable, "leftJoin() -> targetTable should be not null");
        requireNonNull(targetColumn, "leftJoin() -> targetColumn should be not null");
        requireNonNull(targetColumn, "leftJoin() -> from should be not null");
        requireNonNull(targetColumn, "leftJoin() -> column should be not null");

        if (this.from.contains(from)) {
            throw new SelectBuilderException("leftJoin() -> from [" + from + "] found in from list.");
        }

        this.joins.add(new JoinClause(this.database(), JoinType.LEFT, targetTable, targetColumn, from, column));
        return this;
    }

    public SelectBuilder leftJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column, Expression expression) {
        this.joins.add(new JoinClause(this.database(), JoinType.LEFT, targetTable, targetColumn, this.from.get(0), column, expression));
        return this;
    }

    public SelectBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
        this.joins.add(new JoinClause(this.database(), JoinType.INNER, targetTable, targetColumn, this.from.get(0), column));
        return this;
    }

    public SelectBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column, Expression expression) {
        this.joins.add(new JoinClause(this.database(), JoinType.INNER, targetTable, targetColumn, this.from.get(0), column, expression));
        return this;
    }

    public SelectBuilder rightJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
        this.joins.add(new JoinClause(this.database(), JoinType.RIGHT, targetTable, targetColumn, this.from.get(0), column));
        return this;
    }

    public SelectBuilder rightJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column, Expression expression) {
        this.joins.add(new JoinClause(this.database(), JoinType.RIGHT, targetTable, targetColumn, this.from.get(0), column, expression));
        return this;
    }

    private void appendSelect(StringBuilder builder) {
        //if (projection == null) {

        boolean alias = from.size() > 1;

        for (SqlColumn column : this.columns) {



            database().dialect().wrap(builder, column, alias);
            builder.append(',');

            // builder.append('`').append(column.getTable().name()).append("`.");
            //builder.append('`').append(column.name()).append("`,");
        }
        builder.setCharAt(builder.length() - 1, ' ');
//        }
//        else {
//            builder.append(projection.build()).append(' ');
//        }
    }

    private void appendJoins(StringBuilder builder) {

        if (joins.size() == 0) {
            return;
        }
        for (JoinClause clause : joins) {
            clause.build(this, builder);
        }
    }

    private void appendWhere(StringBuilder builder) {
        if (this.where == null) {
            return;
        }
        builder.append(" WHERE ");
        builder.append(this.where.build(database().dialect(), null));
    }

    private void appendOrder(StringBuilder builder) {
        if (this.orderBy.size() == 0) {
            return;
        }

        builder.append(" ORDER BY ");
        boolean addAlias = joins.size() > 0;
        for (Order o : orderBy) {
            database().dialect().wrap(builder, o.column(), hasAlias());
            builder.append(' ').append(o.type().name()).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
    }

    private void appendFrom(StringBuilder builder) {
        builder.append("FROM");
        if (this.from.size() == 1) {
            builder.append(' ');
            builder.append(table(from.get(0), false));
        } else {
            for (SqlTable table : this.from) {
                builder.append(' ');
                builder.append(table(table, true));
                builder.append(',');
            }
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    boolean hasAlias() {
        return this.from.size() > 1;
    }

    private void validate() {

        for (SqlColumn column : this.columns) {

            SqlTable table = column.table();



         //   column.

        }

    }

    private static void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new SelectBuilderException(message);
        }
    }


}