package io.m3.core.ex001;

import com.google.common.eventbus.Subscribe;
import io.m3.core.DomainHandler;
import io.m3.core.ex001.domain.UserAbstractRepository;
import io.m3.core.ex001.gen.event.CreatedUserCommandEvent;
import io.m3.sql.Database;
import io.m3.sql.tx.Transaction;

public class UserDomainHandler implements DomainHandler {

    private final Database database;
    private final UserAbstractRepository repository;

    public UserDomainHandler(Database database) {
        this.database = database;
        this.repository = new UserAbstractRepository(database) {
        };
    }

    @Subscribe
    public void on(CreatedUserCommandEvent command) {

        System.err.println(command);

        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
            this.repository.insert(command.getDomainModel());
            tx.commit();
        }


    }

}