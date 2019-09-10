package io.m3.sql.apt.ex002;

import java.sql.Timestamp;

import io.m3.sql.annotation.BusinessKey;
import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.CreateTimestamp;
import io.m3.sql.annotation.Flyway;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Sequence;
import io.m3.sql.annotation.Table;
import io.m3.sql.annotation.UpdateTimestamp;
import io.m3.sql.id.SequenceGenerator4Long;

@Table( value = "teacher", flyway = @Flyway(version = "1.0.0.002" , description = "init_schema"))
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

    @Column(value = "update_ts", nullable = true)
    @UpdateTimestamp
    Timestamp getUpdateTimestamp();

    void setUpdateTimestamp(Timestamp timestamp);
}