package io.m3.sql.builder;

import io.m3.sql.Database;
import io.m3.sql.desc.SqlTable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class AbstractBuilder {

    private final Database database;

    public AbstractBuilder(Database database) {
        this.database = database;
    }

    protected final Database database() {
        return this.database;
    }

    protected final String table(SqlTable table, boolean alias) {

        if (database.isMonoSchema()) {
            StringBuilder builder = new StringBuilder();
            this.database.dialect().wrap(builder, table, alias);
            return builder.toString();
        }

        return null;
    }
}