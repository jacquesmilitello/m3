package io.m3.sql.builder;

import io.m3.sql.Database;
import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlTable;
import io.m3.sql.expression.Expression;
import io.m3.util.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SelectBuilder extends AbstractBuilder {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SelectBuilder.class);

    private final ImmutableList<SqlColumn> columns;
    //private final Projection projection;
    private Expression where;
    private SqlTable from;
    //private final List<JoinClause> joins = new ArrayList<>();
    //private final List<Order> orderBy = new ArrayList<>();
    private boolean forUpdate = false;
    private int limit = -1;

    public SelectBuilder(Database database, ImmutableList<SqlColumn> columns) {
        super(database);
        this.columns = columns;
        //this.projection = null;
    }

//    public SelectBuilder(Database database, Projection projection) {
//        super(database);
//        this.columns = ImmutableList.of();
//        this.projection = projection;
//    }

    public String build() {
        StringBuilder builder = new StringBuilder(2048);
        builder.append("SELECT ");
        builderSelect(builder);
        builder.append("FROM ");
        builder.append(table(from));
    //    builderJoins(builder);
        builderWhere(builder);
    //    builderOrder(builder);

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

    public SelectBuilder from(SqlTable table) {
        this.from = table;
        return this;
    }

//    public SelectBuilder orderBy(Order order) {
//        this.orderBy.add(order);
//        return this;
//    }

    public SelectBuilder where(Expression expression) {
        this.where = expression;
        return this;
    }

    public SelectBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

//    public SelectBuilder leftJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
//        this.joins.add(new JoinClause(this.database(), JoinType.LEFT, targetTable, targetColumn, this.from, column));
//        return this;
//    }
//
//    public SelectBuilder leftJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column, Expression expression) {
//        this.joins.add(new JoinClause(this.database(), JoinType.LEFT, targetTable, targetColumn, this.from, column, expression));
//        return this;
//    }
//
//    public SelectBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
//        this.joins.add(new JoinClause(this.database(), JoinType.INNER, targetTable, targetColumn, this.from, column));
//        return this;
//    }
//
//    public SelectBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column, Expression expression) {
//        this.joins.add(new JoinClause(this.database(), JoinType.INNER, targetTable, targetColumn, this.from, column, expression));
//        return this;
//    }
//
//    public SelectBuilder rightJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
//        this.joins.add(new JoinClause(this.database(), JoinType.RIGHT, targetTable, targetColumn, this.from, column));
//        return this;
//    }
//
//    public SelectBuilder rightJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column, Expression expression) {
//        this.joins.add(new JoinClause(this.database(), JoinType.RIGHT, targetTable, targetColumn, this.from, column, expression));
//        return this;
//    }

    private void builderSelect(StringBuilder builder) {
        //if (projection == null) {
            for (SqlColumn column : this.columns) {
               // builder.append('`').append(column.getTable().name()).append("`.");
                builder.append('`').append(column.name()).append("`,");
            }
            builder.setCharAt(builder.length() - 1, ' ');
//        }
//        else {
//            builder.append(projection.build()).append(' ');
//        }
    }
//
//    private void builderJoins(StringBuilder builder) {
//
//        if (joins.size() == 0) {
//            return;
//        }
//        for (JoinClause clause : joins) {
//            clause.build(builder);
//        }
//    }

    private void builderWhere(StringBuilder builder) {
        if (this.where == null) {
            return;
        }
        builder.append(" WHERE ");
        builder.append(this.where.build(database().dialect(), null));
    }

//    private void builderOrder(StringBuilder builder) {
//        if (this.orderBy.size() == 0) {
//            return;
//        }
//        builder.append(" ORDER BY ");
//        boolean addAlias = joins.size() > 0;
//        for (Order o : orderBy) {
//            if (addAlias) {
//                builder.append("`").append(from.name()).append("`.");
//            }
//            builder.append("`").append(o.column().name()).append("` ");
//            builder.append(o.type().name()).append(' ');
//        }
//        builder.setCharAt(builder.length() - 1, ' ');
//    }
}
