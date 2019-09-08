package io.m3.core.ex001.domain;

import io.m3.core.DomainModel;
import io.m3.core.annotation.Event;
import io.m3.sql.annotation.BusinessKey;
import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Sequence;
import io.m3.sql.annotation.Table;
import io.m3.sql.id.SequenceGenerator4Long;

@Table("user")
@Event
public interface User extends DomainModel {

    @Sequence("user")
    @PrimaryKey(value = "id", generator = SequenceGenerator4Long.class)
    long getId();

    void setId(long id);

    @BusinessKey
    @Column("name")
    String getName();

    void setName(String name);

    // todo change generator to support parent
    @Column(value = "uuid", updatable = false)
    String getUuid();

    void setUuid(String uuid);

}
