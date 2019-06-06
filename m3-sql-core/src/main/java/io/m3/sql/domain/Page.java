package io.m3.sql.domain;

import java.util.stream.Stream;

public interface Page<T> {

    /**
     * Returns the number of total pages.
     *
     * @return the number of total pages
     */
    int getTotalPages();

    /**
     * Returns the total amount of elements.
     *
     * @return the total amount of elements
     */
    long getTotalElements();

    /**
     * Returns the page content as {@link Stream}.
     */
    Stream<T> getContent();

    public static <T> Page<T> empty() {
        return new PageImpl<>(Stream.empty(), 0, Pageable.unpaged());
    }

    Pageable next();
}
