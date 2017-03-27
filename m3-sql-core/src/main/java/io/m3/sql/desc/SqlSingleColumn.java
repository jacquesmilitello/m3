package io.m3.sql.desc;


import io.m3.util.ImmutableList;

/**
 * @author <a href="mailto:jacques.militello@olky.eu">Jacques Militello</a>
 */
public final class SqlSingleColumn extends SqlColumn {

	private final ImmutableList<SqlColumnProperty> types;


	public SqlSingleColumn(SqlTable table, String name) {
		this(table, name, SqlColumnProperty.INSERTABLE, SqlColumnProperty.UPDATABLE);
	}

	public SqlSingleColumn(SqlTable table, String name, SqlColumnProperty... types) {
		super(table, name);
		this.types = ImmutableList.of(types);
	}

	public ImmutableList<SqlColumnProperty> types() {
		return this.types;
	}

}