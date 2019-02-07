package io.m3.sql.builder;

import io.m3.sql.Database;
import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlTable;
import io.m3.sql.expression.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class JoinClause extends AbstractBuilder {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JoinClause.class);

    private final JoinType type;
    private final SqlTable targetTable;
    private final SqlColumn targetColumn;
    private final SqlColumn column;
    private final Expression expression;

    public JoinClause(Database database, JoinType type, SqlTable targetTable, SqlColumn targetColumn,
                      SqlTable from, SqlColumn column) {
        this(database, type, targetTable, targetColumn, from, column, null);
    }

    public JoinClause(Database database, JoinType type, SqlTable targetTable, SqlColumn targetColumn,
                      SqlTable from, SqlColumn column, Expression expression) {
        super(database);
        this.type = type;
        this.targetTable = targetTable;
        this.targetColumn = targetColumn.fromTable(targetTable);
        this.column = column.fromTable(from);
        this.expression = expression;
    }

    public void build(SelectBuilder selectBuilder, StringBuilder builder) {

        builder.append(' ');
        builder.append(this.type.name()).append(" JOIN ");
        builder.append(this.table(this.targetTable, true));
        builder.append(" ON ");

        database().dialect().wrap(builder, this.targetColumn, true);
        builder.append('=');
        database().dialect().wrap(builder, this.column, true);

        if (this.expression != null) {
            builder.append(" AND ");
            builder.append(this.expression.build(database().dialect(), true));
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Fragment join [{}]", builder.toString());
        }
    }
}