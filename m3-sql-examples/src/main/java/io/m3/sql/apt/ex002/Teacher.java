package io.m3.sql.apt.ex002;

import io.m3.sql.annotation.BusinessKey;
import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Table;
import io.m3.sql.id.SequenceGenerator4Long;

@Table( value = "teacher")
public interface Teacher {

    @PrimaryKey(value = "id", generator = SequenceGenerator4Long.class)
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