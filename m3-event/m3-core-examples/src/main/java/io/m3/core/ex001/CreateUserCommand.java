package io.m3.core.ex001;

import io.m3.core.annotation.CqrsCommand;
import io.m3.core.ex001.domain.User;

@CqrsCommand(domain = User.class)
public interface CreateUserCommand {

    String getName();

    void setName();

    int getAge();

    void setAge(int age);

    String getEmail();

    void setEmail();

}