package coding.toast.batch.job.reader.mapper;

import coding.toast.batch.job.reader.model.Transaction;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.lang.NonNull;

public class TransactionFieldSetMapper implements FieldSetMapper<Transaction> {

	@NonNull
	public Transaction mapFieldSet(FieldSet fieldSet) {
		Transaction trans = new Transaction();
		trans.setAmount(fieldSet.readDouble("amount"));
		trans.setTransactionDate(fieldSet.readDate("transactionDate",
			"yyyy-MM-dd HH:mm:ss"));
		trans.setAccountNumber(fieldSet.readString("accountNumber"));
		return trans;
	}
}
