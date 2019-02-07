package io.m3.sql.builder;

import com.google.common.collect.ImmutableList;
import io.m3.sql.Database;
import io.m3.sql.Dialect;
import io.m3.sql.desc.SqlPrimaryKey;
import io.m3.sql.desc.SqlSingleColumn;
import io.m3.sql.desc.SqlTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InsertBuilder extends AbstractBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsertBuilder.class);

    private final SqlTable table;
    private final ImmutableList<SqlPrimaryKey> keys;
    private final ImmutableList<SqlSingleColumn> columns;

    public InsertBuilder(Database database, SqlTable table, SqlPrimaryKey key,
                         ImmutableList<SqlSingleColumn> columns) {
        this(database, table, ImmutableList.of(key), columns);
    }

    public InsertBuilder(Database database, SqlTable table, ImmutableList<SqlPrimaryKey> keys,
                         ImmutableList<SqlSingleColumn> columns) {
        super(database);
        this.table = table;
        this.keys = keys;
        this.columns = columns;
    }

    public String build() {
        StringBuilder builder = new StringBuilder(2048);
        builder.append("INSERT INTO ");
        builder.append(table(this.table, false));
        builderColumn(builder, this.database().dialect());
        builderValues(builder);

        String sql = builder.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL [{}]", sql);
        }
        return sql;
    }

    private void builderValues(StringBuilder builder) {
        builder.append(" VALUES (");
        for (int i = 0; i < this.keys.size(); i++) {
            builder.append("?,");
        }
        for (SqlSingleColumn column : this.columns) {
            if (column.isInsertable()) {
                builder.append("?,");
            }
        }
        builder.setCharAt(builder.length() - 1, ')');
    }

    private void builderColumn(StringBuilder builder, Dialect dialect) {
        builder.append(" (");
        for (SqlPrimaryKey key : this.keys) {
            dialect.wrap(builder, key, false);
            builder.append(",");
        }
        for (SqlSingleColumn column : this.columns) {
            if (column.isInsertable()) {
                dialect.wrap(builder, column, false);
                builder.append(",");
            }
        }

        builder.setCharAt(builder.length() - 1, ')');
    }

}
