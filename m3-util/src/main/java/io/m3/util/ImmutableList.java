package io.m3.util;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class ImmutableList<E> extends ImmutableCollection<E> implements List<E>{

    
	@SuppressWarnings("unchecked")
    public static <E> ImmutableList<E> of() {
        return (ImmutableList<E>) ImmutableListEmpty.INSTANCE;
    }
	
    @SafeVarargs
    public static <E> ImmutableList<E> of(E... elems) {
        Objects.requireNonNull(elems);
        return new ImmutableListObjectImpl<E>(elems);
    }

	@SafeVarargs
	public static ImmutableList<String> of(String... elems) {
		Objects.requireNonNull(elems);
		return new ImmutableListStringImpl(elems);
	}

	@SafeVarargs
	public static ImmutableList<Integer> of(Integer... elems) {
		Objects.requireNonNull(elems);
		return new ImmutableListIntegerImpl(elems);
	}
    
	/** {@inheritDoc} */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

	/** {@inheritDoc} */
	@Override
	public E remove(int index) {
		throw new ImmutableException("remove",index);
	}
	
	/** {@inheritDoc} */
	@Override
	public E set(int index, E element) {
		throw new ImmutableException("set",index, element);
	}

	/** {@inheritDoc} */
	@Override
	public void add(int index, E element) {
		throw new ImmutableException("add",index, element);
	}

	/** {@inheritDoc} */
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new ImmutableException("addAll",index, c);
	}

	/** {@inheritDoc} */
	@Override
	public Spliterator<E> spliterator() {
		return Spliterators.emptySpliterator();
	}

	/** {@inheritDoc} */
	@Override
	public final void replaceAll(UnaryOperator<E> operator) {
		throw new ImmutableException("replaceAll", operator);
	}

	/** {@inheritDoc} */
	@Override
	public final void sort(Comparator<? super E> c) {
		throw new ImmutableException("sort", c);
	}

}