package io.m3.sql.apt.ex005;

import io.m3.sql.annotation.BusinessKey;
import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.Flyway;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Table;

@Table(value = "Role" , flyway = @Flyway(version = "1.0.0.005" , description = "init_schema"))
public interface Role {

	@PrimaryKey("id")
	int getId();

	void setId(int id);

	@Column(value = "name", length = 64)
	@BusinessKey
	String getName();

	void setName(String name);
	
}
