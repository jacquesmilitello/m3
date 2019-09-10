package io.m3.sql.tx;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionLog  {

    private static final AtomicLong COUNTER = new AtomicLong();

    private final UUID uuid;
    private final AbstractTransaction transaction;
    private final long sequence;
    private final OffsetDateTime start;

	public TransactionLog(AbstractTransaction transaction) {
        this.uuid = UUID.randomUUID();
        this.transaction = transaction;
        this.sequence = COUNTER.incrementAndGet();
        this.start = OffsetDateTime.now();
	}

    public TransactionTracer getTransactionTracer() {
        return null;
    }

	public TransactionSpan rollback() {
		return null;
	}

	public TransactionSpan commit() {
		return null;
	}

	public TransactionSpan close() {
		return null;
	}

}