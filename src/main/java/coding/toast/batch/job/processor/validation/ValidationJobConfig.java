package coding.toast.batch.job.processor.validation;

import coding.toast.batch.job.processor.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ValidationJobConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	
	@Bean("validationJob_ItemReader")
	@StepScope
	public FlatFileItemReader<Customer> customerItemReader(
		@Value("#{jobParameters['customerFile']}") Resource inputFile) {
		
		return new FlatFileItemReaderBuilder<Customer>()
			.name("validationJob_ItemReader")
			.delimited()
			.names(
				"firstNAme",
				"middleInitial",
				"lastName",
				"address",
				"city",
				"state",
				"zip"
			)
			.targetType(Customer.class)
			.resource(inputFile)
			.strict(true)
			.build();
	}
	
	@Bean("validationJob_ItemWriter")
	public ItemWriter<Customer> itemWriter() {
		return chunk -> chunk.getItems().forEach(System.out::println);
	}
	
	@Bean("validationJob_Validation_Processor")
	public BeanValidatingItemProcessor<Customer> customerValidatingItemProcessor() {
		return new BeanValidatingItemProcessor<>();
	}
	
	@Bean("validationJob_copyFileStep")
	public Step copyFileStep() {
		return new StepBuilder("validationJob_copyFileStep", jobRepository)
			.<Customer, Customer>chunk(5, transactionManager)
			.reader(customerItemReader(null))
			.processor(customerValidatingItemProcessor())
			.writer(itemWriter())
			.build();
	}
	
	@Bean("validationJob")
	public Job job() {
		return new JobBuilder("validationJob", jobRepository)
			.start(copyFileStep())
			.build();
	}
	
}
