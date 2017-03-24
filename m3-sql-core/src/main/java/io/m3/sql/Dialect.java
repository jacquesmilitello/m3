package io.m3.sql;

import io.m3.sql.desc.SqlTable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class Dialect {

    public String table(SqlTable table) {
        return table.name();
    }
}
