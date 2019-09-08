package io.m3.core.eventstore;

import io.m3.sql.Database;

final class EventStoreRepository extends EventStoreModelAbstractRepository {

    EventStoreRepository(Database database) {
        super(database);
    }

}
