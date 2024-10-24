package coding.toast.batch.job.stop.support;

import coding.toast.batch.job.stop.domain.Transaction;

import java.util.List;

public interface TransactionDao {
	public List<Transaction> getTransactionsByAccountNumber(String accountNumber);
}
