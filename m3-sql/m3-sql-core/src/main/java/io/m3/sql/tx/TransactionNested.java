package io.m3.sql.tx;

import java.sql.PreparedStatement;

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
    public PreparedStatement read(String sql) {
        return this.parent.read(sql);
    }

    @Override
    public PreparedStatement write(String sql) {
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