package coding.toast.batch.job.stop.support;

import coding.toast.batch.job.stop.domain.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TransactionDaoSupport extends JdbcTemplate implements TransactionDao {
	
	public TransactionDaoSupport(DataSource dataSource) {
		super(dataSource);
	}
	
	@Override
	public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
		return query("""
				select t.id, t.timestamp, t.amount
				from transaction t inner join account_summary a on
				a.id = t.account_summary_id
				where a.account_number = ?""",
			(rs, rowNum) -> {
				Transaction trans = new Transaction();
				trans.setAmount(rs.getDouble("amount"));
				trans.setTimestamp(rs.getDate("timestamp"));
				return trans;
			},
			accountNumber
		);
	}
}
