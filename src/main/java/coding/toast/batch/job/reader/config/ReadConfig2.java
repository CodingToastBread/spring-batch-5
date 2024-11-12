// package coding.toast.batch.job.reader.config;
//
// import coding.toast.batch.job.reader.mapper.TransactionFieldSetMapper;
// import coding.toast.batch.job.reader.model.Customer;
// import coding.toast.batch.job.reader.reader.CustomerFileReader;
// import lombok.RequiredArgsConstructor;
// import org.springframework.batch.core.Job;
// import org.springframework.batch.core.Step;
// import org.springframework.batch.core.configuration.annotation.JobScope;
// import org.springframework.batch.core.configuration.annotation.StepScope;
// import org.springframework.batch.core.job.builder.JobBuilder;
// import org.springframework.batch.core.launch.support.RunIdIncrementer;
// import org.springframework.batch.core.repository.JobRepository;
// import org.springframework.batch.core.step.builder.StepBuilder;
// import org.springframework.batch.item.ItemWriter;
// import org.springframework.batch.item.file.FlatFileItemReader;
// import org.springframework.batch.item.file.MultiResourceItemReader;
// import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
// import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
// import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
// import org.springframework.batch.item.file.mapping.FieldSetMapper;
// import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
// import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
// import org.springframework.batch.item.file.transform.LineTokenizer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.transaction.PlatformTransactionManager;
//
// import java.util.HashMap;
// import java.util.Map;
//
// @Configuration
// @RequiredArgsConstructor
// @SuppressWarnings({"unchecked", "rawtypes"})
// public class ReadConfig2 {
//
// 	private final JobRepository jobRepository;
// 	private final PlatformTransactionManager txManager;
//
// 	@Bean("readConfigJob2")
// 	public Job readConfigJob2() {
// 		return new JobBuilder("readConfigJob2", jobRepository)
// 			.start(readConfigStep())
// 			.incrementer(new RunIdIncrementer())
// 			.build();
// 	}
//
// 	@Bean("readConfigStep2")
// 	public Step readConfigStep() {
// 		return new StepBuilder("readConfigStep2", jobRepository)
// 			.chunk(10, txManager)
// 			// .reader(readConfigItemReader2())
// 			// .reader(customerFileReader())
// 			.reader(multiResourceItemReader())
// 			.writer(readConfigItemWriter2())
// 			.build();
// 	}
//
// 	@Bean
// 	@StepScope
// 	public MultiResourceItemReader multiResourceItemReader() {
// 		return new MultiResourceItemReaderBuilder<>()
// 			.name("multiCustomerReader")
// 			.resources(
// 				new ClassPathResource("job/reader/customerMultiFormat.csv"),
// 				new ClassPathResource("job/reader/customerMultiFormat1.csv"),
// 				new ClassPathResource("job/reader/customerMultiFormat2.csv"))
// 			.delegate(customerFileReader())
// 			.build();
// 	}
//
// 	@Bean("readConfig2FileReader")
// 	@StepScope
// 	public CustomerFileReader customerFileReader() {
// 		// customerFileReader.setResource(new ClassPathResource("job/reader/customerMultiFormat.csv"));
// 		return new CustomerFileReader(readConfigItemReader2());
// 	}
//
// 	@Bean("readConfigItemReader2")
// 	@StepScope
// 	public FlatFileItemReader readConfigItemReader2() {
// 		return new FlatFileItemReaderBuilder()
// 			.name("readConfigItemReader2")
// 			.lineMapper(lineTokenizer())
// 			// .resource(new ClassPathResource("job/reader/customerMultiFormat.csv"))
// 			.build();
// 	}
//
// 	@Bean
// 	public PatternMatchingCompositeLineMapper lineTokenizer() {
// 		Map<String, LineTokenizer> lineTokenizers = new HashMap<>(2);
// 		lineTokenizers.put("CUST*", customerLineTokenizer());
// 		lineTokenizers.put("TRANS*", transactionLineTokenizer());
//
// 		Map<String, FieldSetMapper> fieldSetMappers = new HashMap<>(2);
//
// 		BeanWrapperFieldSetMapper<Customer> customerFieldSetMapper = new BeanWrapperFieldSetMapper<>();
// 		customerFieldSetMapper.setTargetType(Customer.class);
//
// 		fieldSetMappers.put("CUST*", customerFieldSetMapper);
// 		fieldSetMappers.put("TRANS*", new TransactionFieldSetMapper());
//
// 		PatternMatchingCompositeLineMapper lineMappers = new PatternMatchingCompositeLineMapper();
//
// 		lineMappers.setTokenizers(lineTokenizers);
// 		lineMappers.setFieldSetMappers(fieldSetMappers);
//
// 		return lineMappers;
// 	}
//
// 	@Bean
// 	public DelimitedLineTokenizer transactionLineTokenizer() {
// 		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
// 		tokenizer.setNames("prefix", "accountNumber", "transactionDate", "amount");
// 		return tokenizer;
// 	}
//
//
// 	@Bean
// 	public DelimitedLineTokenizer customerLineTokenizer() {
// 		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
// 		tokenizer.setNames(
// 			"firstName",
// 			"middleInitial",
// 			"lastName",
// 			"address",
// 			"city",
// 			"state",
// 			"zipCode"
// 		);
//
// 		tokenizer.setIncludedFields(1,2,3,4,5,6,7); // 첫번째(0)는 안 넣는 이유가 prefix 로 사용되는 CUST 는 매핑시키지 않기 위함이다.
// 		return tokenizer;
// 	}
//
// 	@Bean("readConfigItemWriter2")
// 	public ItemWriter readConfigItemWriter2() {
// 		return chunk -> chunk.getItems().forEach(System.out::println);
// 	}
//
// }
