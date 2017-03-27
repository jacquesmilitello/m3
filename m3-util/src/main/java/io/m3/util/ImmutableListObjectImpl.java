package io.m3.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ImmutableListObjectImpl<E> extends ImmutableList<E> {

	private final E[] elems;

	private final int size;

	public ImmutableListObjectImpl(E[] elems) {
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
		E[] elems = this.elems;
		for (int i = 0, n = size; i < n; i++) {
			if (elems[i].equals(o)) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			int index = 0;

			/** {@inheritDoc} */
			@Override
			public boolean hasNext() {
				return index < size;
			}

			/** {@inheritDoc} */
			@Override
			public E next() {
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
	public E get(int index) {
		if (index < this.size) {
			return this.elems[index];
		}
		throw new IndexOutOfBoundsException("index [" + index + "]");
	}

	/** {@inheritDoc} */
	@Override
	public int indexOf(Object o) {
		E[] elems = this.elems;
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
		E[] elems = this.elems;
		for (int i = size - 1; i > -1 ; i--) {
			if (elems[i].equals(o)) {
				return i;
			}
		}
		return -1;
	}
	
	/** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        int i = 0;
        for (Object element : this) {
            a[i++] = (T) element;
        }
        if (a.length > size()) {
            a[i++] = null;
        }
        return a;
    }

	/** {@inheritDoc} */
	@Override
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	
}