package io.m3.sql.domain;

final class PageableImpl implements Pageable {

    private final int page;
    private final int size;

    PageableImpl(int page, int size) {
        this.page = page;
        this.size = size;
    }

    @Override
    public int getPageSize() {
        return this.size;
    }

    @Override
    public int getPageNumber() {
        return this.page;
    }

    @Override
    public Pageable next() {
        return new PageableImpl(page+1, size);
    }
}
