package io.m3.sql.builder;

import io.m3.sql.Database;
import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlPrimaryKey;
import io.m3.sql.desc.SqlSingleColumn;
import io.m3.sql.desc.SqlTable;
import io.m3.util.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InsertBuilder extends AbstractBuilder{

	/**
	 * SLF4J Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(InsertBuilder.class);

	private final Database database;
	private final SqlTable table;
	private final ImmutableList<SqlPrimaryKey> keys;
	private final ImmutableList<SqlSingleColumn> columns;

	public InsertBuilder(Database database, SqlTable table, SqlPrimaryKey key, ImmutableList<SqlSingleColumn> columns) {
		this(database,table, ImmutableList.of(key), columns);
	}

	public InsertBuilder(Database database, SqlTable table, ImmutableList<SqlPrimaryKey> keys, ImmutableList<SqlSingleColumn> columns) {
		super(database);
		this.database = database;
		this.table = table;
		this.keys = keys;
		this.columns = columns;
	}

	public String build() {
		StringBuilder builder = new StringBuilder(2048);
		builder.append("INSERT INTO ");
		builder.append(table(this.table, false));
		builderColumn(builder);
		builderValues(builder);

		String sql = builder.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SQL [{}]", sql);
		}
		return sql;
	}

	private void builderValues(StringBuilder builder) {
		builder.append(" VALUES (");
		for (SqlPrimaryKey key : this.keys) {
			builder.append("?,");
		}
		for (SqlSingleColumn column : this.columns) {
			if (column.isInsertable()) {
				builder.append("?,");
			}
		}
		builder.setCharAt(builder.length() - 1, ')');
	}

	private void builderColumn(StringBuilder builder) {
		builder.append(" (");
		for (SqlPrimaryKey key : this.keys) {
				builder.append("`");
				builder.append(key.name());
				builder.append("`,");
		}
		for (SqlSingleColumn column : this.columns) {
			if (column.isInsertable()) {
				builder.append("`");
				builder.append(column.name());
				builder.append("`,");
			}
		}

		builder.setCharAt(builder.length() - 1, ')');
	}
//
//	private StringBuilder table(SqlTable table) {
//		StringBuilder builder = new StringBuilder(64);
//		String schema = database.getSchema(table);
//		if (!Strings.isEmpty(schema)) {
//			builder.append("`");
//			builder.append(schema);
//			builder.append("`.");
//		}
//		builder.append("`");
//		builder.append(table.name());
//		builder.append("`");
//		return builder;
//	}

}
