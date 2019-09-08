package io.m3.core;

public interface CommandHandler<C extends Command> {

    DomainDataEvent handle(C command);

}