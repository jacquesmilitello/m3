package io.m3.core.annotation;

import io.m3.core.Command;

public @interface CqrsCommandHandler {

    Class<? extends Command> command();

}
