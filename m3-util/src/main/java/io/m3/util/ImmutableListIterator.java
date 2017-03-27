package io.m3.util;

import java.util.ListIterator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class ImmutableListIterator<T> extends ImmutableIterator<T> implements ListIterator<T> {

    /** {@inheritDoc} */
    @Override
    public void set(T t) {
        throw new ImmutableException("set", t);
    }

    /** {@inheritDoc} */
    @Override
    public void add(T t) {
        throw new ImmutableException("add", t);
    }

}