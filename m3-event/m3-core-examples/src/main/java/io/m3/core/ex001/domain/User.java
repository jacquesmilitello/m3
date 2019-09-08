package io.m3.core.ex001.domain;

import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Table;

@Table("user")
public interface User {

    @PrimaryKey("id")
    String getId();

    void setId(String id);

    @Column("name")
    String getName();

    void setName(String name);

}