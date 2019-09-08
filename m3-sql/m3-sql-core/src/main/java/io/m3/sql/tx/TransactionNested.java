package io.m3.sql.tx;

import io.m3.sql.jdbc.M3PreparedStatement;

import java.sql.Timestamp;

final class TransactionNested implements Transaction {

    private final Transaction parent;

    TransactionNested(Transaction parent) {
        this.parent = parent;
    }

    @Override
    public boolean isReadOnly() {
        return parent.isReadOnly();
    }

    @Override
    public void commit() {
        // skip
    }

    @Override
    public void rollback() {
        // skip
    }

    @Override
    public void close() {
        // skip
    }

    @Override
    public Timestamp timestamp() {
        return this.parent.timestamp();
    }

    @Override
    public M3PreparedStatement read(String sql) {
        return this.parent.read(sql);
    }

    @Override
    public M3PreparedStatement write(String sql) {
        return this.parent.write(sql);
    }

    @Override
    public void addHook(Runnable runnable) {
        parent.addHook(runnable);
    }

    @Override
    public Transaction innerTransaction(TransactionDefinition definition) {
        return null;
    }

}