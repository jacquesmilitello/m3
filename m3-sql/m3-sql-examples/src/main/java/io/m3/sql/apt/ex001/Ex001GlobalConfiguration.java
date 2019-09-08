package io.m3.sql.apt.ex001;

import io.m3.sql.annotation.Database;
import io.m3.sql.annotation.FlywayConfiguration;
import io.m3.sql.annotation.GlobalConfiguration;

@GlobalConfiguration(flywayConfiguration = @FlywayConfiguration(database = {Database.H2 }, packages = "io.m3.sql.apt.ex001"))
public interface Ex001GlobalConfiguration {

}