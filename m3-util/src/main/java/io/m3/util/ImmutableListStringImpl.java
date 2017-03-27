package io.m3.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ImmutableListStringImpl extends ImmutableList<String> {

	private final String[] elems;

	private final int size;

	public ImmutableListStringImpl(String[] elems) {
		this.elems = elems;
		this.size = elems.length;
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return this.size;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object o) {
		String[] elems = this.elems;
		for (int i = 0, n = size; i < n; i++) {
			if (elems[i].equals(o)) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<String> iterator() {
		return new Iterator<String>() {
			int index = 0;

			/** {@inheritDoc} */
			@Override
			public boolean hasNext() {
				return index < size;
			}

			/** {@inheritDoc} */
			@Override
			public String next() {
				return elems[index++];
			}
		};
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		Object[] elems = new Object[size];
		System.arraycopy(this.elems, 0, elems, 0, size);
		return elems;
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(T[] a) {
		System.arraycopy(this.elems, 0, a, 0, size);
		return a;
	}

	/** {@inheritDoc} */
	@Override
	public String get(int index) {
		if (index < this.size) {
			return this.elems[index];
		}
		throw new IndexOutOfBoundsException("index [" + index + "]");
	}

	/** {@inheritDoc} */
	@Override
	public int indexOf(Object o) {
		String[] elems = this.elems;
		for (int i = 0, n = size; i < n; i++) {
			if (elems[i].equals(o)) {
				return i;
			}
		}
		return -1;
	}

	/** {@inheritDoc} */
	@Override
	public int lastIndexOf(Object o) {
		String[] elems = this.elems;
		for (int i = size - 1; i > -1 ; i--) {
			if (elems[i].equals(o)) {
				return i;
			}
		}
		return -1;
	}

	/** {@inheritDoc} */
	@Override
	public ListIterator<String> listIterator() {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public ListIterator<String> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public List<String> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	
}