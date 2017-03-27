package io.m3.util;

import java.util.Collections;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ImmutableIterators {
	
	private static final ImmutableIterator<Object> EMPTY_LIST_ITERATOR = new ImmutableIterator<Object>() {
		/** {@inheritDoc} */
		@Override
		public boolean hasNext() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public Object next() {
			throw new NoSuchElementException();
		}
	};

	private ImmutableIterators() {
	}

	/**
	 * Returns the empty iterator.
	 */
	@SuppressWarnings("unchecked")
	public static <T> ImmutableIterator<T> emptyIterator() {
		return (ImmutableIterator<T>) EMPTY_LIST_ITERATOR;
	}

	/**
	 * Returns an iterator containing only {@code value}.
	 * <p>
	 * The {@link Iterable} equivalent of this method is {@link Collections#singleton}.
	 */
	public static <T> ImmutableIterator<T> singletonIterator(final T value) {
		return new ImmutableIterator<T>() {
			boolean done;

			/** {@inheritDoc} */
			@Override
			public boolean hasNext() {
				return !done;
			}

			/** {@inheritDoc} */
			@Override
			public T next() {
				if (done) {
					throw new NoSuchElementException();
				}
				done = true;
				return value;
			}
		};
	}

}