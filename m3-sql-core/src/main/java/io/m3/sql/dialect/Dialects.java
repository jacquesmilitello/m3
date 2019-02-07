package io.m3.sql.dialect;

import io.m3.sql.Database;
import io.m3.sql.Dialect;

public final class Dialects {

    private Dialects() {
    }

    public static Dialect h2(Database database) {
        return new H2Dialect(database);
    }

    public static Dialect dialect(Dialect.Name name, Database database) {
        switch (name) {
            case H2:
                return h2(database);
            default:
                throw new IllegalStateException("invalid dialect name [" + name + "]");
        }
    }
}
