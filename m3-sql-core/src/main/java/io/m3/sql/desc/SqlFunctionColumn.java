package io.m3.sql.desc;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SqlFunctionColumn extends SqlColumn {

    private final String function;

    public SqlFunctionColumn(String function, SqlColumn column) {
        super(column.table(), column.name());
        this.function = function;
    }

    public String toSql() {
        return new StringBuilder().append(this.function).append('(').append(name()).append(')').toString();
    }

    @Override
    protected SqlColumn newColumFromAlias(SqlTable targetTable) {
        return null;
    }

}
