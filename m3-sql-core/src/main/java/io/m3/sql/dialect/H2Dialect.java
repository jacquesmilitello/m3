package io.m3.sql.dialect;

import io.m3.sql.Dialect;

public final class H2Dialect extends Dialect {

    @Override
    public String nextVal(String sequence) {
        return "nextval('"+sequence + "')";
    }
}
