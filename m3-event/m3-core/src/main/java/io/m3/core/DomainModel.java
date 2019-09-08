package io.m3.core;

import io.m3.sql.annotation.Column;

public interface DomainModel {

    @Column("uuid")
    String getUuid();

    void setUuid(String uuid);
}
