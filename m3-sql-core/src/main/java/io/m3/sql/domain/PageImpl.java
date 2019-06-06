package io.m3.sql.domain;

import java.util.stream.Stream;

public final class PageImpl<T> implements Page<T> {

    private final Stream<T> content;
    private final long total;
    private final Pageable pageable;

    public PageImpl(Stream<T> content, long total, Pageable pageable) {
        this.content = content;
        this.total = total;
        this.pageable = pageable;
    }

    @Override
    public int getTotalPages() {
        return (int) ((total/pageable.getPageSize()) + 1);
    }

    @Override
    public long getTotalElements() {
        return this.total;
    }

    @Override
    public Stream<T> getContent() {
        return this.content;
    }

    @Override
    public Pageable next() {
        return pageable.next();
    }

}
