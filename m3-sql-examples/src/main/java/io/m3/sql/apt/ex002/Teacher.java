package io.m3.sql.apt.ex002;

import io.m3.sql.annotation.BusinessKey;
import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Table;

@Table( value = "teacher")
public interface Teacher {

    @PrimaryKey("id")
    long getId();

    void setId(long id);

    @Column("code")
    @BusinessKey
    String getCode();

    void setCode(String code);

    @Column("prefix_code")
    @BusinessKey
    String getPrefixCode();

    void setPrefixCode(String prefixCode);

}