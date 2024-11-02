package coding.toast.batch.job.reader.model;

import lombok.*;

import java.io.ObjectOutput;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
	private String firstName;
	private String middleInitial;
	private String lastName;
	private String address; // csv test
	// private String addressNumber;// fixed length test
	// private String street;
	private String city;
	private String state;
	private String zipCode;
	
	private List<Transaction> transactions;
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		
		output.append(firstName);
		output.append(" ");
		output.append(middleInitial);
		output.append(". ");
		output.append(lastName);
		
		if (transactions != null && !transactions.isEmpty()) {
			output.append(" has ");
			output.append(transactions.size());
			output.append(" transactions.");
		} else {
			output.append(" has no transactions.");
		}
		
		return output.toString();
	}
}
