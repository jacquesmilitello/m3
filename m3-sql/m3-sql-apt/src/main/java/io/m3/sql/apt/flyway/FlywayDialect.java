package io.m3.sql.apt.flyway;

import io.m3.sql.annotation.Column;

public interface FlywayDialect {

	String toSqlType(String string, Column column);

}
