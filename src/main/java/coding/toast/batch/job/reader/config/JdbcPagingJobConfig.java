package coding.toast.batch.job.reader.config;

import coding.toast.batch.job.reader.model.jdbc.Customer;
import coding.toast.batch.job.reader.rowmapper.CustomerRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@RequiredArgsConstructor
public class JdbcPagingJobConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	
	@Bean
	Job jdbcPagingJobConfigJob() {
		return new JobBuilder("jdbcPagingJobConfigJob", jobRepository)
			.start(jdbcPagingJobConfigStep())
			.incrementer(new RunIdIncrementer())
			.build();
	}
	
	AtomicInteger count = new AtomicInteger(0);
	
	@Bean
	Step jdbcPagingJobConfigStep() {
		return new StepBuilder("jdbcPagingJobConfigStep", jobRepository)
			.<Customer, Customer>chunk(10, transactionManager)
			.reader(customerItemReader(null, null))
			.writer(chunk -> {
				int i = count.addAndGet(1);
				System.out.println("Current Chunk Count is .... " + i);
				for (Customer item : chunk.getItems()) {
					System.out.println("item = " + item);
				}
			})
			.build();
	}
	
	private final DataSource dataSource;
	
	@Bean("jdbcPagingJobConfigReader")
	@StepScope
	JdbcPagingItemReader<Customer> customerItemReader(
		PagingQueryProvider queryProvider,
		@Value("#{jobParameters['city']}") String city
	) {
		return new JdbcPagingItemReaderBuilder<Customer>()
			.name("jdbcPagingJobConfigReader")
			.dataSource(dataSource)
			.queryProvider(queryProvider)
			// .parameterValues(Collections.singletonMap("city", city))
			.pageSize(10)
			.rowMapper(new CustomerRowMapper())
			.build();
	}
	
	@Bean
	public SqlPagingQueryProviderFactoryBean pagingQueryProviderFactoryBean() {
		PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
		SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
		factoryBean.setSelectClause("select *");
		factoryBean.setFromClause("from customer");
		// factoryBean.setWhereClause("where city = :city");
		factoryBean.setSortKey("id");
		factoryBean.setDataSource(dataSource);
		return factoryBean;
	}
	
	
	
}
