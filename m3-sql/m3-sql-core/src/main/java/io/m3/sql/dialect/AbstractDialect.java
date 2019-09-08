package io.m3.sql.dialect;

import io.m3.sql.Database;
import io.m3.sql.Dialect;
import io.m3.sql.M3SqlException;
import io.m3.sql.Module;
import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlTable;
import io.m3.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

abstract class AbstractDialect implements Dialect {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDialect.class);

    private final Database database;

    public AbstractDialect(Database database) {
        this.database = database;
    }

    final String prefix(SqlTable table) {
        Module module = this.database.getModule(table);
        if (module == null) {
            throw new M3SqlException("Module not found for table [" + table + "]");
        }
        requireNonNull(this.database.getModule(table), "table [" + table);
        if (Strings.isEmpty(module.catalog())) {
            return table.name();
        } else {
            return module.catalog().concat(".").concat(table.name());
        }
    }

    @Override
    public void wrap(Appendable appendable, SqlTable table, boolean alias) {
        try {
            appendable.append(prefix(table));
            if (alias) {
                appendable.append(aliasSeparator());
                appendable.append(table.alias());
            }
        } catch (IOException cause) {
            LOGGER.warn("Failed to wrap({},{},{}) -> [{}]", appendable, table, alias, cause);
        }
    }

    @Override
    public void wrap(Appendable appendable, SqlColumn column, boolean alias) {
        try {
            if (alias) {
                appendable.append(column.table().alias());
                appendable.append('.');
            }

            appendable.append(column.toSql());
        } catch (IOException cause) {
            LOGGER.warn("Failed to wrap({},{},{}) -> [{}]", appendable, column, alias, cause);
        }
    }

    protected abstract String aliasSeparator();
}
