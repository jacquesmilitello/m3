package io.m3.core.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.m3.core.DomainDataEvent;
import io.m3.core.EventStore;
import io.m3.sql.Database;
import io.m3.sql.tx.Transaction;

import java.sql.Timestamp;

public class H2EventStore implements EventStore {

    private final Database database;
    private final EventStoreRepository repository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public H2EventStore(Database database) {
        this.database = database;
        this.repository = new EventStoreRepository(database);
    }

    @Override
    public void store(DomainDataEvent event) {


        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {

            EventStoreModel model = Factory.newEventStoreModel();
            model.setAggregateType(event.getDomain());
            model.setAggregateId(event.getDomainId());
            model.setCreatedAt(Timestamp.from(event.getInstant()));
            try {
                model.setEvent(MAPPER.writeValueAsString(event.getDomainModel()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            this.repository.insert(model);

            tx.commit();
        }

    }

}
