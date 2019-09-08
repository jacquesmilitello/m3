package io.m3.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public class CommandGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandGateway.class);

   //private final CommandHandler<CreateUserCommand> commandHandler;
    private final EventBus eventBus;
    private final EventStore eventStore;

    private final CommandHandlerRegistry registry;

    public CommandGateway(EventStore eventStore, EventBus eventBus) {
     //   this.commandHandler = commandHandler;
        this.eventStore = eventStore;
        this.eventBus = eventBus;
        this.registry = ServiceLoader.load(CommandHandlerRegistry.class).iterator().next();
    }

    public <T extends Command> void dispatch(T command) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("dispatch{}", command);
        }


        // 1. retrieve command handler
        CommandHandler<T> ch = (CommandHandler<T>) registry.get(command.getClass());

        // 2. handler
        DomainDataEvent dde = ch.handle(command);

        // 3. store
        this.eventStore.store(dde);

        // 4. dispatch
        eventBus.publish(dde);


    }

}
