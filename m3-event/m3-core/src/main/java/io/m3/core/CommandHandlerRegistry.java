package io.m3.core;

public interface CommandHandlerRegistry {

    <T extends Command> CommandHandler<T> get(Class<T> command);

}