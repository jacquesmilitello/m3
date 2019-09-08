package io.m3.sql.apt.flyway;

import java.sql.Timestamp;

import io.m3.sql.annotation.Column;
import io.m3.sql.apt.log.LoggerFactory;

final class FlywayDialectH2 implements FlywayDialect {

	static final FlywayDialect INSTANCE = new FlywayDialectH2();

	@Override
	public String toSqlType(String javaType, Column column) {

		if (int.class.getName().equals(javaType) || Integer.class.getName().equals(javaType)) {
			return "INT";
		}
		
		if (long.class.getName().equals(javaType) || Long.class.getName().equals(javaType)) {
			return "BIGINT";
		}
		
		if (String.class.getName().equals(javaType)) {
			return "VARCHAR(" + column.length() + ")";
		}
		
		if (Timestamp.class.getName().equals(javaType)) {
			return "TIMESTAMP";
		}
		
		LoggerFactory.getInstance().getLogger(FlywayDialectH2.class).error("No sql type for java type [" + javaType + "]");
		return null;
	}
}
