package io.m3.sql.builder;

import io.m3.sql.Database;
import io.m3.sql.desc.SqlTable;
import io.m3.sql.expression.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DeleteBuilder extends AbstractBuilder {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteBuilder.class);

    private final Database database;
    private final SqlTable table;
    private Expression where;

    public DeleteBuilder(Database database, SqlTable table) {
        super(database);
        this.database = database;
        this.table = table;
    }

    public DeleteBuilder where(Expression expression) {
        this.where = expression;
        return this;
    }

    public String build() {
        String sql = "DELETE FROM " +
                table(this.table, false) +
                " WHERE " +
                where.build(database.dialect(), false);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL [{}]", sql);
        }

        return sql;
    }


}