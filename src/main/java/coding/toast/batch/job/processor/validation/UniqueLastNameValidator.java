package coding.toast.batch.job.processor.validation;

import coding.toast.batch.job.processor.model.Customer;
import org.springframework.batch.item.*;
import org.springframework.batch.item.util.ExecutionContextUserSupport;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.lang.NonNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class UniqueLastNameValidator implements ItemStream, Validator<Customer> {
	private final ExecutionContextUserSupport executionContextUserSupport = new ExecutionContextUserSupport();
	
	private Set<String> lastNames = new HashSet<>();
	
	@Override
	public void validate(@NonNull Customer value) throws ValidationException {
		if (lastNames.contains(value.getLastName())) {
			throw new ValidationException("Duplicate last name was found: " + value.getLastName());
		}
		this.lastNames.add(value.getLastName());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@NonNull
	public void open(@NonNull ExecutionContext executionContext) throws ItemStreamException {
		String lastNames = getExecutionContextKey("lastNames");
		if (executionContext.containsKey(lastNames)) {
			this.lastNames = (Set<String>) executionContext.get(lastNames);
		}
	}
	
	@Override
	@NonNull
	public void update(@NonNull ExecutionContext executionContext) throws ItemStreamException {
		Iterator<String> iterator = lastNames.iterator();
		Set<String> copiedLastNames = new HashSet<>();
		while (iterator.hasNext()) {
			copiedLastNames.add(iterator.next());
		}
		executionContext.put(getExecutionContextKey("lastNames"), copiedLastNames);
	}
	
	public void setName(String name) {
		this.setExecutionContextName(name);
	}
	
	public String getName() {
		return executionContextUserSupport.getName();
	}
	
	protected void setExecutionContextName(String name) {
		executionContextUserSupport.setName(name);
	}
	
	public String getExecutionContextKey(String key) {
		return executionContextUserSupport.getKey(key);
	}
	
	
}
