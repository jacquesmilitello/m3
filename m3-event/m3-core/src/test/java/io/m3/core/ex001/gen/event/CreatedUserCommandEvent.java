package io.m3.core.ex001.gen.event;

import io.m3.core.DomainDataEvent;
import io.m3.core.ex001.domain.User;

import java.time.Instant;

// Created + UserCommand + Event
public final class CreatedUserCommandEvent implements DomainDataEvent<User> {

    private final User user;
    private final Instant instant;

    public CreatedUserCommandEvent(User user) {
        this.user = user;
        this.instant = Instant.now();
    }

    // generated for @Event
    @Override
    public String getDomain() {
        return "user";
    }

    // @primaryKey
    @Override
    public String getDomainId() {
        return user.getUuid();
    }

    @Override
    public User getDomainModel() {
        return user;
    }

    @Override
    public Instant getInstant() {
        return this.instant;
    }

}
