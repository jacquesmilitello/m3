package io.m3.sql.builder;

import io.m3.sql.desc.SqlPrimaryKey;
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
	private final ImmutableList<SqlColumn> columns;
	private Expression where;
	
	public UpdateBuilder(Database database, SqlTable table, ImmutableList<SqlColumn> columns) {
		super(database);
		this.database = database;
		this.table = table;
		this.columns = columns;
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
		} else {
		//	builder.append(where.build());
		}

		String sql = builder.toString();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SQL [{}]", sql);
		}
		
		return sql;
	}

	private void builderSetValues(StringBuilder builder) {
		builder.append(" SET ");
		for (SqlColumn column : this.columns) {

			//if (!column.types().contains(SqlColumnProperty.ID) && column.types().contains(SqlColumnProperty.UPDATABLE)) {
				builder.append("`");
				builder.append(column.name());
				builder.append("`=?,");
//			} else {
//				if (LOGGER.isDebugEnabled()) {
//					LOGGER.debug("Skip column [{}] -> because type : [{}]", column, column.types());
//				}
//			}

		}
		builder.setCharAt(builder.length() - 1, ' ');
	}

	private void builderWherePk(StringBuilder builder) {
		for (SqlColumn column : this.columns) {

			if (column instanceof SqlPrimaryKey) {
				builder.append("`");
				builder.append(column.name());
				builder.append("`=?");
			}

//			if (column.types().contains(SqlColumnProperty.ID)) {
//				builder.append("`");
//				builder.append(column.name());
//				builder.append("`=?");
//				break;
//			} else {
//				if (LOGGER.isDebugEnabled()) {
//					LOGGER.debug("Skip column [{}] -> because type : [{}]", column, column.types());
//				}
//			}
		}
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
