package io.m3.core.ex001.handler;

import io.m3.core.CommandHandler;
import io.m3.core.DomainDataEvent;
import io.m3.core.annotation.CqrsCommandHandler;
import io.m3.core.ex001.command.CreateUserCommand;
import io.m3.core.ex001.domain.Factory;
import io.m3.core.ex001.domain.User;
import io.m3.core.ex001.domain.UserDescriptor;
import io.m3.core.ex001.gen.event.CreatedUserCommandEvent;
import io.m3.sql.Database;
import io.m3.sql.id.SequenceGenerator4Long;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


@CqrsCommandHandler(command = CreateUserCommand.class)
public class CreateUserCommandHandler implements CommandHandler<CreateUserCommand> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserCommand.class);


    @Override
    public DomainDataEvent handle(CreateUserCommand command) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("handle ({})", command);
        }

        User user = Factory.newUser();
        user.setName(command.getName());
        user.setUuid(UUID.randomUUID().toString());
        return new CreatedUserCommandEvent(user);

    }

}
