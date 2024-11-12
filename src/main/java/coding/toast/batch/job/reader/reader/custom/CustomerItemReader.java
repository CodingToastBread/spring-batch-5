package coding.toast.batch.job.reader.reader.custom;

import coding.toast.batch.job.reader.model.jdbc.Customer;
import org.springframework.batch.item.*;
import org.springframework.batch.item.util.ExecutionContextUserSupport;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerItemReader /* extends ItemStreamSupport*/ implements ItemStreamReader<Customer> {
	private List<Customer> customers;
	private int curIndex;
	private final String INDEX_KEY = "current.index.customers";
	
	private String [] firstNames = {"Michael", "Warren", "Ann", "Terrence",
		"Erica", "Laura", "Steve", "Larry"};
	private String middleInitial = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String [] lastNames = {"Gates", "Darrow", "Donnelly", "Jobs",
		"Buffett", "Ellison", "Obama"};
	private String [] streets = {"4th Street", "Wall Street", "Fifth Avenue",
		"Mt. Lee Drive", "Jeopardy Lane",
		"Infinite Loop Drive", "Farnam Street",
		"Isabella Ave", "S. Greenwood Ave"};
	private String [] cities = {"Chicago", "New York", "Hollywood", "Aurora",
		"Omaha", "Atherton"};
	private String [] states = {"IL", "NY", "CA", "NE"};
	
	private Random generator = new Random();
	
	public CustomerItemReader() {
		curIndex = 0;
		
		customers = new ArrayList<>();
		
		for (int i = 0; i < 100; i++) {
			customers.add(buildCustomer());
		}
	}
	
	public void setName(String name){
		executionContextUserSupport.setName(name);
	}
	
	private Customer buildCustomer() {
		Customer customer = new Customer();
		customer.setId((long) generator.nextInt(Integer.MAX_VALUE));
		customer.setFirstName(
			firstNames[generator.nextInt(firstNames.length)]);
		customer.setMiddleInitial(
			String.valueOf(middleInitial.charAt(
				generator.nextInt(middleInitial.length()))));
		customer.setLastName(
			lastNames[generator.nextInt(lastNames.length)]);
		customer.setAddress(generator.nextInt(9999) + " " +
		                    streets[generator.nextInt(streets.length)]);
		customer.setCity(cities[generator.nextInt(cities.length)]);
		customer.setState(states[generator.nextInt(states.length)]);
		customer.setZipCode(String.valueOf(generator.nextInt(99999)));
		return customer;
	}
	
	@Override
	public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		Customer cust = null;
		
		if (curIndex == 50) {
			throw new RuntimeException("This will end your execution");
		}
		
		if (curIndex < customers.size()) {
			cust = customers.get(curIndex);
			curIndex++;
		}
		
		return cust;
	}
	
	private final ExecutionContextUserSupport executionContextUserSupport = new ExecutionContextUserSupport();
	
	@Override
	public void open(@NonNull ExecutionContext executionContext) throws ItemStreamException {
		if (executionContext.containsKey(getExecutionContextKey(INDEX_KEY))) {
			int index = executionContext.getInt(getExecutionContextKey(INDEX_KEY));
			if (index == 50) {
				curIndex = 51;
			} else {
				curIndex = index;
			}
		} else {
			curIndex = 0;
		}
	}
	
	@Override
	public void update(@NonNull ExecutionContext executionContext) throws ItemStreamException {
		executionContext.putInt(getExecutionContextKey(INDEX_KEY), curIndex);
	}
	
	@Override
	@NonNull
	public void close() throws ItemStreamException {
	
	}
	
	
	public String getExecutionContextKey(String key) {
		return executionContextUserSupport.getKey(key);
	}
}
