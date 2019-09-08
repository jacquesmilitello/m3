package io.m3.sql.desc;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SqlPrimaryKey extends SqlColumn {

    public SqlPrimaryKey(SqlTable table, String name) {
        super(table, name);
    }


    @Override
    protected SqlColumn newColumFromAlias(SqlTable targetTable) {
        return new SqlPrimaryKey(targetTable, this.name());
    }
}