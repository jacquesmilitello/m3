package io.m3.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ImmutableListEmpty extends ImmutableList<Object> {

	private static final Object[] EMPTY = new Object[0];
	
	/**
	 * Singleton instance.
	 */
	static final ImmutableListEmpty INSTANCE = new ImmutableListEmpty();
	
	private ImmutableListEmpty() {
	}
	
	/** {@inheritDoc} */
	@Override
	public int size() {
		return 0;
	}
	
	/** {@inheritDoc} */
	@Override
	public Object get(int index) {
		throw new IndexOutOfBoundsException("Immutable list Empty -> index [" + index + "]");
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
	public Iterator<Object> iterator() {
		return ImmutableIterators.emptyIterator();
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		return EMPTY;
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(T[] a) {
		return a;
	}

	/** {@inheritDoc} */
	@Override
	public int indexOf(Object o) {
		return -1;
	}

	/** {@inheritDoc} */
	@Override
	public int lastIndexOf(Object o) {
		return -1;
	}

	/** {@inheritDoc} */
	@Override
	public ListIterator<Object> listIterator() {
		return ImmutableListIterators.emptyListIterator();
	}

	/** {@inheritDoc} */
	@Override
	public ListIterator<Object> listIterator(int index) {
		throw new IndexOutOfBoundsException("Immutable list is empty");
	}

	/** {@inheritDoc} */
	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		throw new IndexOutOfBoundsException("Immutable list is empty");
	}
}
