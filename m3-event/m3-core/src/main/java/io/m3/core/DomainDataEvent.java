package io.m3.core;

import java.time.Instant;

public interface DomainDataEvent<T extends DomainModel> {

    String getDomain();

    String getDomainId();

    T getDomainModel();

    Instant getInstant();
}
