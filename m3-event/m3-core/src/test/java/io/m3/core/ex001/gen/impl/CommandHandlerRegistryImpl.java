package io.m3.core.ex001.gen.impl;

import com.google.common.collect.ImmutableMap;
import io.m3.core.Command;
import io.m3.core.CommandHandler;
import io.m3.core.CommandHandlerRegistry;
import io.m3.core.ex001.handler.CreateUserCommandHandler;

public final class CommandHandlerRegistryImpl implements CommandHandlerRegistry {

    private static final ImmutableMap<Class<? extends Command>, CommandHandler> HANDLERS;

    static {
        HANDLERS = ImmutableMap.<Class<? extends Command>, CommandHandler>builder()
                .put(CreateUserCommandImpl.class, new CreateUserCommandHandler())
                .build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Command> CommandHandler<T> get(Class<T> command) {
        return HANDLERS.get(command);
    }
}