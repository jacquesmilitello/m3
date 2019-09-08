package io.m3.core.eventstore;

import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.CreateTimestamp;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Sequence;
import io.m3.sql.annotation.Table;
import io.m3.sql.id.SequenceGenerator4Long;

import java.sql.Timestamp;

@Table(value = "m3_event_store")
public interface EventStoreModel {

    @Sequence("seq__m3_event_store")
    @PrimaryKey(value = "id", generator = SequenceGenerator4Long.class)
    long getId();

    void setId(long id);

    @Column(value = "aggregate_type", updatable = false)
    String getAggregateType();

    void setAggregateType(String aggregate);

    @Column(value = "aggregate_id", updatable = false)
    String getAggregateId();

    void setAggregateId(String aggregateId);

    @Column(value = "trace_id", updatable = false)
    String getTraceId();

    void setTraceId(String traceId);

    @Column(value = "event", updatable = false)
    String getEvent();

    void setEvent(String event);

    @Column(value = "created_at", updatable = false)
    Timestamp getCreatedAt();

    void setCreatedAt(Timestamp createdAt);

}
