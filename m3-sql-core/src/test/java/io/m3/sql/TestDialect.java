package io.m3.sql;

import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlSequence;
import io.m3.sql.desc.SqlTable;

/**
 * Created by jmilitello on 26/03/2017.
 */
public class TestDialect implements Dialect {
    @Override
    public String nextVal(SqlSequence sequence) {
        return null;
    }

    @Override
    public void wrap(Appendable appendable, SqlTable table, boolean alias) {

    }

    @Override
    public void wrap(Appendable appendable, SqlColumn targetColumn, boolean alias) {

    }

    @Override
    public String range(int offset, int limit) {
        return null;
    }
}
