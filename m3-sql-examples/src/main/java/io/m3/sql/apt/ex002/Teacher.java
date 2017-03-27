package io.m3.sql.apt.ex002;

import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Table;

@Table( value = "teacher")
public interface Teacher {

    @PrimaryKey("id")
    long getId();

    void setId(long id);

}