// package coding.toast.batch.job.reader.config;
//
// import coding.toast.batch.job.reader.mapper.CustomFieldSetMapper;
// import coding.toast.batch.job.reader.model.Customer;
// import coding.toast.batch.job.reader.tokenizer.CustomerFileLineTokenizer;
// import lombok.RequiredArgsConstructor;
// import org.springframework.batch.core.Job;
// import org.springframework.batch.core.Step;
// import org.springframework.batch.core.job.builder.JobBuilder;
// import org.springframework.batch.core.launch.support.RunIdIncrementer;
// import org.springframework.batch.core.repository.JobRepository;
// import org.springframework.batch.core.step.builder.StepBuilder;
// import org.springframework.batch.item.Chunk;
// import org.springframework.batch.item.ItemReader;
// import org.springframework.batch.item.ItemWriter;
// import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
// import org.springframework.batch.item.file.transform.Range;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.transaction.PlatformTransactionManager;
// import org.springframework.util.StringUtils;
//
// import java.io.*;
// import java.nio.charset.StandardCharsets;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.util.stream.Stream;
//
// @Configuration
// @RequiredArgsConstructor
// public class ReadConfig1 {
//
// 	private final JobRepository jobRepository;
// 	private final PlatformTransactionManager txManager;
//
// 	@Bean("readConfigJob1")
// 	public Job readConfigJob1() {
// 		return new JobBuilder("readConfigJob1", jobRepository)
// 			.start(readConfigStep())
// 			.incrementer(new RunIdIncrementer())
// 			.build();
// 	}
//
// 	@Bean("readConfigStep1")
// 	public Step readConfigStep() {
// 		return new StepBuilder("readConfigStep1", jobRepository)
// 			.<Customer, Customer>chunk(10, txManager)
// 			.reader(readConfigItemReader2())
// 			.writer(readConfigItemWriter())
// 			.build();
// 	}
//
// 	@Bean("readConfigItemReader1")
// 	public ItemReader<Customer> readConfigItemReader1() {
// 		return new FlatFileItemReaderBuilder<Customer>()
// 			.name("readConfigItemReader1")
// 			.resource(new ClassPathResource("job/reader/customerFixedWidth.txt")).strict(true)
// 			.fixedLength()
// 			.columns(
// 				new Range(1, 11), new Range(12, 12),
// 				new Range(13, 22), new Range(23, 26), new Range(27, 46), new Range(47, 62),
// 				new Range(63, 64), new Range(65, 69))
// 			.names(
// 				"firstName",
// 				"middleInitial",
// 				"lastName",
// 				"addressNumber",
// 				"street",
// 				"city",
// 				"state",
// 				"zipCode")
// 			.targetType(Customer.class)
// 			.build();
// 	}
//
// 	@Bean("readConfigItemReader2")
// 	public ItemReader<Customer> readConfigItemReader2() {
// 		return new FlatFileItemReaderBuilder<Customer>()
// 			.name("readConfigItemReader2")
// 			.resource(new ClassPathResource("job/reader/customer.csv")).strict(true)
// 			/*.delimited()
// 			.delimiter(",")
// 			.names( // FieldSet 을 만들때 사용하는 key 값들
// 				"firstName",
// 				"middleInitial",
// 				"lastName",
// 				"addressNumber",
// 				"street",
// 				"city",
// 				"state",
// 				"zipCode")*/
// 			// .fieldSetMapper(new CustomFieldSetMapper())
// 			.lineTokenizer(new CustomerFileLineTokenizer())
// 			.targetType(Customer.class) // setMapper 를 Bean~~Mapper 로 설정해 줌
// 			.build();
// 	}
//
// 	private static String[] readCsvHeader(String classPath) {
// 		if (StringUtils.hasText(classPath)) {
// 			return null;
// 		}
//
// 		String[] header;
// 		try (
// 			InputStream inputStream = new ClassPathResource(classPath).getInputStream();
// 			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
// 			BufferedReader bufferedReader = new BufferedReader(inputStreamReader))
// 		{
// 			String firstLine = bufferedReader.readLine();
// 			header = firstLine.split(",", -1);
// 		} catch (IOException e) {
// 			throw new RuntimeException(e);
// 		}
// 		return header;
// 	}
//
// 	@Bean("readConfigItemWriter1")
// 	private static ItemWriter<Customer> readConfigItemWriter() {
// 		return (Chunk<? extends Customer> chunk) -> {
// 			for (Customer item : chunk.getItems()) {
// 				System.out.println("item = " + item);
// 			}
// 		};
// 	}
//
//
// }
