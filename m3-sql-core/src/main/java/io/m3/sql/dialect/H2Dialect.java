package io.m3.sql.dialect;

import io.m3.sql.Dialect;
import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlSequence;
import io.m3.sql.desc.SqlTable;

final class H2Dialect implements Dialect {

    @Override
    public String nextVal(SqlSequence sequence) {
        return "SELECT NEXTVAL('"+ sequence.name() + "')";
    }

    @Override
    public void wrap(Appendable appendable, SqlTable table, boolean alias) {

    }

    @Override
    public void wrap(Appendable appendable, SqlColumn targetColumn, boolean alias) {

    }
}
