package io.m3.util;

import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ImmutableListIterators {

    private ImmutableListIterators() {
    }

    private static final ImmutableListIterator<Object> EMPTY_LIST_ITERATOR = new ImmutableListIterator<Object>() {
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

        /** {@inheritDoc} */
        @Override
        public boolean hasPrevious() {
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public Object previous() {
            return new NoSuchElementException();
        }

        /** {@inheritDoc} */
        @Override
        public int nextIndex() {
            return -1;
        }

        /** {@inheritDoc} */
        @Override
        public int previousIndex() {
            return -1;
        }
    };


    /**
     * Returns the empty iterator.
     */
    @SuppressWarnings("unchecked")
    public static <T> ImmutableListIterator<T> emptyListIterator() {
        return (ImmutableListIterator<T>) EMPTY_LIST_ITERATOR;
    }

}
