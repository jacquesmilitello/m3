package io.m3.sql.dialect;

import io.m3.sql.Database;
import io.m3.sql.desc.SqlSequence;

final class H2Dialect extends AbstractDialect {

    public H2Dialect(Database database) {
        super(database);
    }

    @Override
    protected String aliasSeparator() {
        return " AS ";
    }

    @Override
    public String nextVal(SqlSequence sequence) {
        return "SELECT NEXTVAL('" + sequence.name() + "')";
    }

}
