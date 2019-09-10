package io.m3.sql.apt.ex005;

import io.m3.sql.annotation.Database;
import io.m3.sql.annotation.FlywayConfiguration;
import io.m3.sql.annotation.GlobalConfiguration;

@GlobalConfiguration(flywayConfiguration = @FlywayConfiguration(database = {Database.H2 }, packages = "io.m3.sql.apt.ex005"))
public interface Ex005GlobalConfiguration {

}