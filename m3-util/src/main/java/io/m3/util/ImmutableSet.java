package io.m3.util;

import java.util.Set;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class ImmutableSet<E> extends ImmutableCollection<E> implements Set<E> {

    @SuppressWarnings("unchecked")
    public static <E> ImmutableSet<E> of() {
        return (ImmutableSet<E>) ImmutableSetEmpty.INSTANCE;
    }

    /**
     * Returns an immutable set containing a single element.
     */
    public static <E> ImmutableSet<E> of(E element) {
        return new ImmutableSetSingleton<>(element, element.hashCode());
    }


}