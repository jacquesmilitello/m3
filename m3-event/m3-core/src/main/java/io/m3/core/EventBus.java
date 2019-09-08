package io.m3.core;


public interface EventBus {

   void publish(DomainDataEvent event);

}
