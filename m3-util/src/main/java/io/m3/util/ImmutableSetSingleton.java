package io.m3.util;

import java.util.Collection;
import java.util.Set;

/**
 * @author <a href="mailto:j.militello@olkypay.eu">Jacques Militello</a>
 */
final class ImmutableSetSingleton<E> extends ImmutableSet<E> {

	/**
	 *	Default serial uid. 
	 */
	private static final long serialVersionUID = 1L;
	
	private final E e;
	
	private final int hashCode;

	ImmutableSetSingleton(E e, int hashcode) {
		if (e == null) {
			throw new NullPointerException();
		}
		this.e = e;
		this.hashCode = hashcode;
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return 1;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object obj) {
		return e.equals(obj);
	}

	/** {@inheritDoc} */
	@Override
	public ImmutableIterator<E> iterator() {
		return ImmutableIterators.singletonIterator(e);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.hashCode;
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		return new Object[] { e };
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(T[] a) {
		a[0] = (T) e;
		return a;
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsAll(Collection<?> c) {
		if (c == null) {
			return false;
		}
		if (c.size() != 1) {
			return false;
		}
		return this.e.equals(c.iterator().next());
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof Set) {
			Set<?> that = (Set<?>) object;
			return that.size() == 1 && e.equals(that.iterator().next());
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("e[0]",this.e).toString();
	}

}
