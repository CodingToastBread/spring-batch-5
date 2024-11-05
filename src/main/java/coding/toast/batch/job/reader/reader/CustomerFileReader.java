package coding.toast.batch.job.reader.reader;

import coding.toast.batch.job.reader.model.Customer;
import coding.toast.batch.job.reader.model.Transaction;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import java.util.ArrayList;

public class CustomerFileReader implements ResourceAwareItemReaderItemStream<Customer> {
	private Object curItem = null;
	
	private final ResourceAwareItemReaderItemStream<Object> delegate;
	
	public CustomerFileReader(ResourceAwareItemReaderItemStream<Object> delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (curItem == null) {
			curItem = delegate.read();
		}
		
		Customer item = (Customer) curItem;
		curItem = null;
		
		
		if (item != null) {
			item.setTransactions(new ArrayList<>());
			
			while (peek() instanceof Transaction) {
				item.getTransactions().add((Transaction) curItem);
				curItem = null;
			}
		}
		return item;
	}
	
	private Object peek() throws Exception {
		if (curItem == null) {
			curItem = delegate.read();
		}
		return curItem;
	}
	
	@Override
	public void open(@NonNull ExecutionContext executionContext) throws ItemStreamException {
		delegate.open(executionContext);
	}
	
	@Override
	public void update(@NonNull ExecutionContext executionContext) throws ItemStreamException {
		delegate.update(executionContext);
	}
	
	@Override
	public void close() throws ItemStreamException {
		delegate.close();
	}
	
	@Override
	public void setResource(@NonNull Resource resource) {
		this.delegate.setResource(resource);
	}
}
