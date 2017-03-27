package io.m3.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class ImmutableIterator<E> implements Iterator<E> , Immutable {

	protected ImmutableIterator() {
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		throw new ImmutableException("remove");
	}

	/** {@inheritDoc} */
	@Override
	public final void forEachRemaining(Consumer<? super E> action) {
		Objects.requireNonNull(action);
		while (hasNext()) {
			action.accept(next());
		}
	}

}