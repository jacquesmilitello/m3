package io.m3.sql.domain;

enum Unpaged implements Pageable {

    INSTANCE;

    @Override
    public int getPageSize() {
        return -1;
    }

    @Override
    public int getPageNumber() {
        return -1;
    }

    @Override
    public Pageable next() {
        return INSTANCE;
    }
}