package io.m3.sql.tx;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionSynchronizationManager {

	private static final ThreadLocal<Transaction> TRANSACTIONS = new ThreadLocal<>();
	
	private TransactionSynchronizationManager() {
	}

	public static Transaction currentTransaction() {
		return TRANSACTIONS.get();
	}

	public static void setTransaction(Transaction tx) {
		TRANSACTIONS.set(tx);
	}

	public static void clear() {
		TRANSACTIONS.remove();
	}

	
}
