package io.m3.sql.apt.ex001;

import io.m3.sql.annotation.AutoIncrement;
import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Table;

@Table( value = "auto_increment_pojo")
public interface AutoIncrementPojo {

    @PrimaryKey("id")
    @AutoIncrement
    int getId();

    void setId(int id);

    @Column("name")
    String getName();

    void setName(String name);
}
