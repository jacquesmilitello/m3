package io.m3.core.annotation;

import io.m3.core.CommandHandler;
import io.m3.core.DomainHandler;
import io.m3.core.DomainModel;

public @interface CqrsCommand {

    CqrsCommandType type();

    Class<? extends DomainModel> domain();

    //Class<? extends CommandHandler> commandHandler();

    //Class<? extends DomainHandler> domainHandler();

}
