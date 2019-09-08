package io.m3.sql.domain;

/**
 * Abstract interface for pagination information.
 *
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Pageable {

    static Pageable unpaged() {
        return Unpaged.INSTANCE;
    }

    int getPageSize();

    int getPageNumber();

    Pageable next();

    public static Pageable of(int page, int size) {
        return new PageableImpl(page, size);
    }

}
