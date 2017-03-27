package io.m3.util;

import java.util.Collection;
import java.util.Set;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ImmutableSetEmpty extends ImmutableSet<Object> {

	/**
	 * Serial uid.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Singleton Instance.
	 */
	static final ImmutableSetEmpty INSTANCE = new ImmutableSetEmpty();

	/**
	 * Hide constructor.
	 */
	private ImmutableSetEmpty() {
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object o) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsAll(Collection<?> c) {
		return c != null && c.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public ImmutableIterator<Object> iterator() {
		return ImmutableIterators.emptyIterator();
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		return Arrays.EMPTY;
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(T[] a) {
		return a;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof Set) {
			return ((Set<?>) object).isEmpty();
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "[]";
	}

}