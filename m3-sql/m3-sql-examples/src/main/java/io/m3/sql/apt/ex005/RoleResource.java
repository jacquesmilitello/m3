package io.m3.sql.apt.ex005;

import io.m3.sql.annotation.Flyway;
import io.m3.sql.annotation.JoinColumn;
import io.m3.sql.annotation.JoinTable;

@JoinTable(value = "role_resource", flyway = @Flyway(version = "1.0.0.005", description = "init_schema"))
public interface RoleResource {

	@JoinColumn(value = "role_id", target = Role.class)
	int getRoleId();

	void setRoleId(int roleId);

	@JoinColumn(value = "resource_id", target = Resource.class)
	int getResourceId();

	void setResourceId(int resourceId);

}
