package io.m3.sql.apt.flyway;

import io.m3.sql.annotation.Column;

public interface FlywayDialect {

	String toSqlType(String string, Column column);

	String autoIncrementType(String string, Column annotation);
	
	String wrap(String value);

	

}
