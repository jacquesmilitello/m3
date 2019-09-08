package io.m3.sql.apt.ex002;

import io.m3.sql.annotation.*;
import io.m3.sql.id.SequenceGenerator4Long;

import java.sql.Timestamp;

@Table( value = "teacher")
public interface Teacher {

    @PrimaryKey(value = "id", generator = SequenceGenerator4Long.class)
    @Sequence("seq_teacher")
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

    @Column("create_ts")
    @CreateTimestamp
    Timestamp getCreationTimestamp();

    void setCreationTimestamp(Timestamp timestamp);

    @Column("update_ts")
    @UpdateTimestamp
    Timestamp getUpdateTimestamp();

    void setUpdateTimestamp(Timestamp timestamp);
}