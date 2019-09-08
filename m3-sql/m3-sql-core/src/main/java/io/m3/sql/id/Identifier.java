package io.m3.sql.id;

import io.m3.sql.M3SqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Identifier<T> {

    T next() throws M3SqlException;
}
