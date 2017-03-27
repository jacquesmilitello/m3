package io.m3.util;

import java.util.Collection;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class ImmutableCollection<E> implements Collection<E>, Immutable {
	
	/** {@inheritDoc} */
	@Override
	public final boolean add(E e) {
		throw new ImmutableException("add",e);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean remove(Object o) {
		throw new ImmutableException("remove",o);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new ImmutableException("addAll",c);
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new ImmutableException("removeAll",c);
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		throw new ImmutableException("clear");
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new ImmutableException("retainAll",c);
	}
	
}