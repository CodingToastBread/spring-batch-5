package coding.toast.batch.job.reader.mapper;

import coding.toast.batch.job.reader.model.Customer;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;

public class CustomFieldSetMapper implements FieldSetMapper<Customer> {
	@NonNull
	@Override
	public Customer mapFieldSet(@NonNull FieldSet fieldSet) throws BindException {
		Customer customer = new Customer();
		customer.setAddress(fieldSet.readString("addressNumber") + " " + fieldSet.readString("street"));
		customer.setCity(fieldSet.readString("city"));
		customer.setFirstName(fieldSet.readString("firstName"));
		customer.setMiddleInitial(fieldSet.readString("middleInitial"));
		customer.setLastName(fieldSet.readString("lastName"));
		customer.setState(fieldSet.readString("state"));
		customer.setZipCode(fieldSet.readString("zipCode"));
		return customer;
	}
}
