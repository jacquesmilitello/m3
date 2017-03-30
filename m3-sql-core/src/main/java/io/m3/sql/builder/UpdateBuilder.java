package io.m3.sql.builder;

import io.m3.sql.desc.SqlPrimaryKey;
import io.m3.sql.desc.SqlSingleColumn;
import io.m3.sql.expression.Expression;
import io.m3.util.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.m3.sql.Database;
import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlTable;

import javax.management.Query;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class UpdateBuilder extends AbstractBuilder {

	/**
	 * SLF4J Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateBuilder.class);

	private final Database database;
	private final SqlTable table;
	private final ImmutableList<SqlSingleColumn> columns;
	private final ImmutableList<SqlPrimaryKey> keys;
	private Expression where;
	
	public UpdateBuilder(Database database, SqlTable table, ImmutableList<SqlSingleColumn> columns, ImmutableList<SqlPrimaryKey> keys) {
		super(database);
		this.database = database;
		this.table = table;
		this.columns = columns;
		this.keys = keys;
	}

	public UpdateBuilder where(Expression expression) {
        this.where = expression;
        return this;
    }
	
	public String build() {
		StringBuilder builder = new StringBuilder(2048);
		builder.append("UPDATE ");
		builder.append(table(this.table, false));
		builderSetValues(builder);
		builder.append(" WHERE ");

		if (this.where == null) {
			builderWherePk(builder);
		} //else {
			//TODO builder.append(where.build());
		//}

		String sql = builder.toString();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SQL [{}]", sql);
		}
		
		return sql;
	}

	private void builderSetValues(StringBuilder builder) {
		builder.append(" SET ");
		for (SqlSingleColumn column : this.columns) {
			if (column.isUpdatable()) {
				builder.append("`");
				builder.append(column.name());
				builder.append("`=?,");
			}

		}
		builder.setCharAt(builder.length() - 1, ' ');
	}

	private void builderWherePk(StringBuilder builder) {
		for (SqlPrimaryKey column : this.keys) {
				builder.append("`");
				builder.append(column.name());
				builder.append("`=?");
		}
	}

}
