package io.m3.core.ex001.command;

import io.m3.core.Command;
import io.m3.core.annotation.CqrsCommand;
import io.m3.core.ex001.domain.User;

import static io.m3.core.annotation.CqrsCommandType.CREATE;

@CqrsCommand(type = CREATE, domain = User.class)
public interface UpdateUserCommand extends Command {

    String getName();

    void setName(String name);

    int getAge();

    void setAge(int age);

    String getEmail();

    void setEmail(String email);

}