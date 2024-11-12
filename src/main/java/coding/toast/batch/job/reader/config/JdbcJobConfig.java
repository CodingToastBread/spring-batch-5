package coding.toast.batch.job.reader.config;

import coding.toast.batch.job.reader.model.jdbc.Customer;
import coding.toast.batch.job.reader.rowmapper.CustomerRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcJobConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	
	@Bean
	Job jdbcJobConfigJob() {
		return new JobBuilder("jdbcJobConfigJob", jobRepository)
			.start(jdbcJobConfigStep())
			.build();
	}
	
	@Bean
	Step jdbcJobConfigStep() {
		return new StepBuilder("jdbcJobConfigStep", jobRepository)
			.<Customer, Customer>chunk(10, transactionManager)
			.reader(customerItemReader())
			.writer(chunk -> {
				for (Customer item : chunk.getItems()) {
					System.out.println("item = " + item);
				}
			})
			.build();
	}
	
	private final DataSource dataSource;
	
	@Bean("jdbcJobConfigReader")
	JdbcCursorItemReader<Customer> customerItemReader() {
		return new JdbcCursorItemReaderBuilder<Customer>()
			.name("jdbcJobConfigReader")
			.dataSource(dataSource)
			.sql("select * from customer where city = ?")
			.preparedStatementSetter(citySetter(null))
			.rowMapper(new CustomerRowMapper())
			.build();
	}
	
	
	@Bean
	@StepScope
	ArgumentPreparedStatementSetter citySetter(
		@Value("#{jobParameters['city']}") String city
	){
		return new ArgumentPreparedStatementSetter(new Object[]{city});
	}
}
