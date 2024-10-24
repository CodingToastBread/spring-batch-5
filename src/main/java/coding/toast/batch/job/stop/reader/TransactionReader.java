package coding.toast.batch.job.stop.reader;

import coding.toast.batch.job.stop.domain.Transaction;
import lombok.Setter;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.lang.NonNull;

public class TransactionReader implements ItemStreamReader<Transaction> {
	
	@Setter
	private ItemStreamReader<FieldSet> fieldSetReader;
	private int recordCount = 0;
	private int expectedRecordCount = 0;
	
	private StepExecution stepExecution;
	
	public TransactionReader(ItemStreamReader<FieldSet> fieldSetReader) {
		this.fieldSetReader = fieldSetReader;
	}
	
	@Override
	public Transaction read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		/*if (this.recordCount == 25) {
			throw new ParseException("This isn't what i want");
		}*/
		return process(fieldSetReader.read());
	}
	
	private Transaction process(FieldSet fieldSet) {
		Transaction result = null;
		
		if (fieldSet != null) {
			if (fieldSet.getFieldCount() > 1) {
				result = new Transaction();
				result.setAccountNumber(fieldSet.readString(0));
				result.setTimestamp(fieldSet.readDate(1, "yyyy-MM-DD HH:mm:ss"));
				result.setAmount(fieldSet.readDouble(2));
				recordCount++;
			} else {
				expectedRecordCount = fieldSet.readInt(0);
				// return result  null 이므로 더 이상 읽을 데이터가 남아있지 않다는 의미다.
				
				if (expectedRecordCount != this.recordCount) {
					this.stepExecution.setTerminateOnly();
				}
			}
		}
		return result;
	}

	/*@AfterStep
	public ExitStatus afterStep(StepExecution execution) {
		if (recordCount == expectedRecordCount) {
			return execution.getExitStatus();
		} else {
			return ExitStatus.STOPPED;
		}
	}*/
	
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}
	
	@Override
	public void open(@NonNull ExecutionContext executionContext) throws ItemStreamException {
		this.fieldSetReader.open(executionContext);
	}
	
	@Override
	public void update(@NonNull ExecutionContext executionContext) throws ItemStreamException {
		this.fieldSetReader.update(executionContext);
	}
	
	@Override
	public void close() throws ItemStreamException {
		this.fieldSetReader.close();
	}
}
