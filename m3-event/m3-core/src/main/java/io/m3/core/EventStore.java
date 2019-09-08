package io.m3.core;

public interface EventStore {

    void store(DomainDataEvent event);

}
