package io.m3.sql.builder;

import com.google.common.collect.ImmutableList;
import io.m3.sql.Database;
import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlTable;
import io.m3.sql.expression.AggregateFunction;
import io.m3.sql.expression.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SelectBuilder extends AbstractBuilder {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SelectBuilder.class);

    private static final String LAMBDA_ARROW_OPERATOR = " -> ";
    private static final String LEFT_JOIN = "leftJoin()" + LAMBDA_ARROW_OPERATOR;
    private static final String RIGHT_JOIN = "rightJoin()" + LAMBDA_ARROW_OPERATOR;
    private static final String INNER_JOIN = "innerJoin()" + LAMBDA_ARROW_OPERATOR;

    private static final String NOT_NULL = " should be not null";
    private static final String TARGET_TABLE_NN = "targetTable" + NOT_NULL;
    private static final String TARGET_COLUMN_NN = "targetColumn" + NOT_NULL;
    private static final String FROM_NN = "from" + NOT_NULL;
    private static final String COLUMN_NN = "column" + NOT_NULL;
    private static final String EXPRESSION_NN = "expression " + LAMBDA_ARROW_OPERATOR;

    private final ImmutableList<SqlColumn> columns;
    private final AggregateFunction aggregateFunction;
    private Expression where;
    private ImmutableList<SqlTable> from;
    private final List<JoinClause> joins = new ArrayList<>();
    private final List<Order> orderBy = new ArrayList<>();
    private ImmutableList<SqlColumn> groupBy;
    private boolean forUpdate;
    private int limit = -1;
    private int offset = -1;

    public SelectBuilder(Database database, ImmutableList<SqlColumn> columns) {
        super(database);
        forUpdate = false;
        this.columns = columns;
        this.aggregateFunction = null;

    }

    public SelectBuilder(Database database, AggregateFunction function) {
        super(database);
        forUpdate = false;
        this.columns = ImmutableList.of();
        this.aggregateFunction = function;
    }

    public String build() {

        validate();

        StringBuilder builder = new StringBuilder(2048);

        appendSelect(builder);
        appendFrom(builder);
        appendJoins(builder);
        appendWhere(builder);
        appendGroupBy(builder);
        appendOrder(builder);

        if (forUpdate) {
            builder.append(" FOR UPDATE");
        }

        if (limit != -1 && offset != -1) {
            builder.append(' ').append(this.database().dialect().range(offset, limit));
        } else if (limit != -1) {
            builder.append(" LIMIT ").append(this.limit);
        } else {

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

    public SelectBuilder from(SqlTable... tables) {
        this.from = ImmutableList.copyOf(tables);
        return this;
    }

    public SelectBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    public SelectBuilder orderBy(Order... orderings) {
        for (Order o : orderings) {
            this.orderBy.add(o);
        }
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

    public SelectBuilder groupBy(SqlColumn... columns) {
        this.groupBy = ImmutableList.copyOf(columns);
        return this;
    }

    /**
     * Used when : select * from [_from_ JOIN_targetTable_ ON ]
     *
     * @param targetTable  is the first table to be joined
     * @param targetColumn is the _targetColumn_ used for the 'ON'
     * @param column       is the _from_ column used for the 'ON'
     * @return
     */
    public SelectBuilder leftJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
        requireNonNull(targetTable, LEFT_JOIN + TARGET_TABLE_NN);
        requireNonNull(targetColumn, LEFT_JOIN + TARGET_COLUMN_NN);
        requireNonNull(column, LEFT_JOIN + COLUMN_NN);

        if (this.from == null) {
            throw new SelectBuilderException(LEFT_JOIN + " call from() before this.");
        }

        if (!this.from.contains(column.table())) {
            throw new SelectBuilderException(LEFT_JOIN + " join column [" + column + "] not found in from clause.");
        }

        this.joins
                .add(new JoinClause(this.database(), JoinType.LEFT, targetTable, targetColumn, column.table(), column));
        return this;
    }

    /**
     * Used when : select * from _from_ on xxx JOIN _targetTable_ ON _otherColumn_=_targetColumn_
     *
     * @param targetTable  is the first table to be joined
     * @param targetColumn is the _targetColumn_ used for the 'ON'
     * @param otherFrom    is the first table to be joined
     * @param otherColumn  is the _from_ column used for the 'ON'
     * @return
     */
    public SelectBuilder leftJoin(SqlTable targetTable, SqlColumn targetColumn, SqlTable otherFrom,
                                  SqlColumn otherColumn) {
        requireNonNull(targetTable, LEFT_JOIN + TARGET_TABLE_NN);
        requireNonNull(targetColumn, LEFT_JOIN + TARGET_COLUMN_NN);
        requireNonNull(otherFrom, LEFT_JOIN + FROM_NN);
        requireNonNull(otherColumn, LEFT_JOIN + COLUMN_NN);

        if (this.from.contains(otherFrom)) {
            throw new SelectBuilderException(LEFT_JOIN + " from [" + otherFrom + "] found in from list.");
        }

        this.joins
                .add(new JoinClause(this.database(), JoinType.LEFT, targetTable, targetColumn, otherFrom, otherColumn));
        return this;
    }

    public SelectBuilder leftJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column,
                                  Expression expression) {
        requireNonNull(targetTable, LEFT_JOIN + TARGET_TABLE_NN);
        requireNonNull(targetColumn, LEFT_JOIN + TARGET_COLUMN_NN);
        requireNonNull(column, LEFT_JOIN + COLUMN_NN);
        requireNonNull(expression, LEFT_JOIN + EXPRESSION_NN);
        this.joins.add(new JoinClause(this.database(), JoinType.LEFT, targetTable, targetColumn, column.table(), column, expression));
        return this;
    }

    public SelectBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
        return innerJoinAlias(targetTable, targetColumn, column.table(), column);
    }

    public SelectBuilder innerJoinAlias(SqlTable targetTable, SqlColumn targetColumn, SqlTable fromTable, SqlColumn fromColumn) {
        requireNonNull(targetTable, INNER_JOIN + TARGET_TABLE_NN);
        requireNonNull(targetColumn, INNER_JOIN + TARGET_COLUMN_NN);
        requireNonNull(fromColumn, INNER_JOIN + COLUMN_NN);
        this.joins.add(new JoinClause(this.database(), JoinType.INNER, targetTable, targetColumn, fromTable, fromColumn));
        return this;
    }

    public SelectBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column, Expression expression) {
        return innerJoinAlias(targetTable, targetColumn, column.table(), column, expression);
    }

    public SelectBuilder innerJoinAlias(SqlTable targetTable, SqlColumn targetColumn, SqlTable fromTable, SqlColumn fromColumn, Expression expression) {
        requireNonNull(targetTable, INNER_JOIN + TARGET_TABLE_NN);
        requireNonNull(targetColumn, INNER_JOIN + TARGET_COLUMN_NN);
        requireNonNull(fromColumn, INNER_JOIN + COLUMN_NN);
        this.joins.add(new JoinClause(this.database(), JoinType.INNER, targetTable, targetColumn, fromTable, fromColumn, expression));
        return this;
    }

    public SelectBuilder rightJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
        requireNonNull(targetTable, RIGHT_JOIN + TARGET_TABLE_NN);
        requireNonNull(targetColumn, RIGHT_JOIN + TARGET_COLUMN_NN);
        requireNonNull(column, RIGHT_JOIN + COLUMN_NN);
        this.joins.add(
                new JoinClause(this.database(), JoinType.RIGHT, targetTable, targetColumn, column.table(), column));
        return this;
    }

    public SelectBuilder rightJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column,
                                   Expression expression) {
        requireNonNull(targetTable, RIGHT_JOIN + TARGET_TABLE_NN);
        requireNonNull(targetColumn, RIGHT_JOIN + TARGET_COLUMN_NN);
        requireNonNull(column, RIGHT_JOIN + COLUMN_NN);
        requireNonNull(expression, RIGHT_JOIN + EXPRESSION_NN);
        this.joins.add(new JoinClause(this.database(), JoinType.RIGHT, targetTable, targetColumn, column.table(),
                column, expression));
        return this;
    }

    private void appendSelect(StringBuilder builder) {
        builder.append("SELECT ");
        boolean alias = hasAlias();

        // Aggregate Functions
        if (aggregateFunction != null) {
            builder.append(aggregateFunction.build(database().dialect(), alias));
            builder.append(' ');
        } else {
            // OR Columns
            for (SqlColumn column : this.columns) {
                database().dialect().wrap(builder, column, alias);
                builder.append(',');
            }
            builder.setCharAt(builder.length() - 1, ' ');
        }
    }

    private void appendJoins(StringBuilder builder) {
        if (joins.isEmpty()) {
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
        builder.append(this.where.build(database().dialect(), hasAlias()));
    }

    private void appendOrder(StringBuilder builder) {
        if (this.orderBy.isEmpty()) {
            return;
        }
        builder.append(" ORDER BY ");
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
            builder.append(table(from.get(0), (!joins.isEmpty())));
        } else {
            for (SqlTable table : this.from) {
                builder.append(' ');
                builder.append(table(table, true));
                builder.append(',');
            }
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    private void appendGroupBy(StringBuilder builder) {
        if (this.groupBy == null) {
            return;
        }
        builder.append(" GROUP BY ");

        for (SqlColumn column : this.groupBy) {
            database().dialect().wrap(builder, column, hasAlias());
            builder.append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
    }

    boolean hasAlias() {
        return this.from.size() > 1 || !joins.isEmpty();
    }

    private void validate() {
        for (SqlColumn column : this.columns) {
            SqlTable table = column.table();
            table.alias();
        }
    }

    private static void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new SelectBuilderException(message);
        }
    }
}