package coding.toast.batch.job.reader.model;

import lombok.Data;
import lombok.ToString;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class Transaction {
	private String accountNumber;
	private Date transactionDate;
	private Double amount;
	
	@ToString.Exclude
	private DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	
	public String getDateString() {
		return this.formatter.format(this.transactionDate);
	}
	
}
