package coding.toast.batch.job.reader.rowmapper;

import coding.toast.batch.job.reader.model.jdbc.Customer;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRowMapper implements RowMapper<Customer> {
	@Override
	public Customer mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
		Customer customer = new Customer();
		BeanPropertyRowMapper<Customer> rowMapper = new BeanPropertyRowMapper<>();
		rowMapper.setMappedClass(Customer.class);
		return rowMapper.mapRow(rs, rowNum);
		
		// record 면 DataClassRowMapper 사용
		
		// customer.setId(rs.getLong("id"));
		// customer.setAddress(rs.getString("id"));
		// customer.setCity(rs.getLong("id"));
		// customer.setFirstName(rs.getLong("id"));
		// customer.setLastName(rs.getLong("id"));
		// customer.setState(rs.getLong("id"));
		// customer.setZipCode(rs.getLong("id"));
	}
}
