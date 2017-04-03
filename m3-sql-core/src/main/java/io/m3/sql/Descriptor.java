package io.m3.sql;

import io.m3.sql.desc.SqlPrimaryKey;
import io.m3.sql.desc.SqlSingleColumn;
import io.m3.sql.desc.SqlTable;
import io.m3.util.ImmutableList;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Descriptor {

    ImmutableList<SqlSingleColumn> columns();

    ImmutableList<SqlPrimaryKey> ids();

    SqlTable table();

}
