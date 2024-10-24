package coding.toast.batch.job.stop.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountSummary {
	private int id;
	private String accountNumber;
	private Double currentBalance;
}
