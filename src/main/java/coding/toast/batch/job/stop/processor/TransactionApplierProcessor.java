package coding.toast.batch.job.stop.processor;

import coding.toast.batch.job.stop.domain.AccountSummary;
import coding.toast.batch.job.stop.domain.Transaction;
import coding.toast.batch.job.stop.support.TransactionDao;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

import java.util.List;

public class TransactionApplierProcessor implements ItemProcessor<AccountSummary, AccountSummary> {
	private final TransactionDao transactionDao;
	
	public TransactionApplierProcessor(TransactionDao transactionDao) {
		this.transactionDao = transactionDao;
	}
	
	@Override
	public AccountSummary process(AccountSummary summary) throws Exception {
		List<Transaction> transactions
			= transactionDao.getTransactionsByAccountNumber(summary.getAccountNumber());
		
		for (Transaction transaction : transactions) {
			summary.setCurrentBalance(summary.getCurrentBalance() + transaction.getAmount());
		}
		return summary;
	}
}
