package io.m3.sql.tx;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionLog  {

    private static final AtomicLong COUNTER = new AtomicLong();

    private final UUID uuid;
    private final AbstractTransaction transaction;
    private final long sequence;

	public TransactionLog(AbstractTransaction transaction) {
        this.uuid = UUID.randomUUID();
        this.transaction = transaction;
        this.sequence = COUNTER.incrementAndGet();
	}

    public TransactionTracer getTransactionTracer() {
        return null;
    }

}