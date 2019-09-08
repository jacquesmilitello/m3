package io.m3.core.ex001;

import io.m3.core.CommandGateway;
import io.m3.core.EventStore;
import io.m3.core.eventstore.H2EventStore;
import io.m3.core.ex001.command.CreateUserCommand;
import io.m3.core.ex001.domain.IoM3CoreEx001DomainModule;
import io.m3.core.ex001.gen.impl.CreateUserCommandImpl;
import io.m3.core.impl.InMemoryEventBus;
import io.m3.sql.Database;
import io.m3.sql.Dialect;
import io.m3.sql.impl.DatabaseImpl;
import io.m3.sql.tx.TransactionManagerImpl;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Ex001Test {

    private JdbcConnectionPool ds;
    private Database database;

    @BeforeEach
    void before() {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        database = new DatabaseImpl(ds, Dialect.Name.H2, new TransactionManagerImpl(ds), "", new  io.m3.core.eventstore.IoM3CoreEventstoreModule("m3", "")
        ,new IoM3CoreEx001DomainModule("domain", ""));
        Flyway flyway = Flyway.configure().dataSource(ds).load();
        flyway.migrate();
    }

    @Test
    void test() {

        CreateUserCommand command = new CreateUserCommandImpl();
        command.setAge(39);
        command.setName("Jacques");
        command.setEmail("jm@mail.org");

        CreateUserCommand command2 = new CreateUserCommandImpl();
        command2.setAge(39);
        command2.setName("Jacques");
        command2.setEmail("jm@mail.org");



        InMemoryEventBus eventBus = new InMemoryEventBus();
        eventBus.register(new UserDomainHandler(database));
        EventStore eventStore = new H2EventStore(database);
        CommandGateway gateway = new CommandGateway(eventStore, eventBus);



        gateway.dispatch(command);
        gateway.dispatch(command2);

      //  CreateUserCommandHandlerGateway gateway = new CreateUserCommandHandlerGateway(new CreateUserCommandHandler(), eventBus);
      //  gateway.dispatch(command);

       // commandGateway.dispatch(command);

       // EventStore store = new H2EventStore(database);

       // store.store(new CreatedUserCommandEvent(command));

    }
}
