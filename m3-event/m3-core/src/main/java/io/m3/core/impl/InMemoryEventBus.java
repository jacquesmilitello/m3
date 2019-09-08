package io.m3.core.impl;

import com.google.common.eventbus.AsyncEventBus;
import io.m3.core.DomainDataEvent;
import io.m3.core.DomainHandler;
import io.m3.core.EventBus;

import java.util.concurrent.Executors;

public final class InMemoryEventBus implements EventBus {

    private final com.google.common.eventbus.EventBus bus = new com.google.common.eventbus.EventBus("event-sourcing");

    @Override
    public void publish(DomainDataEvent event) {
        this.bus.post(event);
    }


    public void register(DomainHandler domainHandler) {
        this.bus.register(domainHandler);
    }
}
