package coding.toast.batch.job.stop;

import coding.toast.batch.job.stop.domain.AccountSummary;
import coding.toast.batch.job.stop.domain.Transaction;
import coding.toast.batch.job.stop.processor.TransactionApplierProcessor;
import coding.toast.batch.job.stop.reader.TransactionReader;
import coding.toast.batch.job.stop.support.TransactionDao;
import coding.toast.batch.job.stop.support.TransactionDaoSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class StopTestConfiguration {
	
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	
	@Bean("stopTestConfiguration-tx-reader")
	@StepScope
	public TransactionReader transactionReader() {
		return new TransactionReader(fileItemReader(null));
	}
	
	@Bean("stopTestConfiguration-file-reader")
	@StepScope
	public ItemStreamReader<FieldSet> fileItemReader(
		@Value("#{jobParameters['transactionFile']}") FileSystemResource inputFile) {
		return new FlatFileItemReaderBuilder<FieldSet>()
			.name("stopTestConfiguration-file-reader")
			.resource(inputFile)
			.lineTokenizer(new DelimitedLineTokenizer())
			.fieldSetMapper(new PassThroughFieldSetMapper())
			.build();
	}
	
	@Bean("stopTestConfiguration-tx-writer")
	public JdbcBatchItemWriter<Transaction> transactionWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Transaction>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("""
			INSERT INTO transaction (account_summary_id, timestamp, amount)
			values ((select id
			        from account_summary
					where account_number = :accountNumber), :timestamp, :amount)
			""")
			.dataSource(dataSource)
			.build();
	}
	
	@Bean("stopTestConfiguration-import-file-step")
	public Step importTransactionFileStep() {
		return new StepBuilder("stopTestConfiguration-import-file-step", jobRepository)
			.<Transaction, Transaction>chunk(10, transactionManager)
			.reader(transactionReader())
			.writer(transactionWriter(null))
			.allowStartIfComplete(true)
			.listener(transactionReader())
			.build();
	}
	
	
	
	
	@Bean("stopTestConfiguration-summary-reader")
	@StepScope // 쓰이는 Step 이 2군데여서 싱글톤 방식 사용방지를 위해서 StepScope 처리했다.
	public JdbcCursorItemReader<AccountSummary> accountSummaryReader(DataSource dataSource) {
		return new JdbcCursorItemReaderBuilder<AccountSummary>()
			.name("stopTestConfiguration-summary-reader")
			.dataSource(dataSource)
			.sql("""
			select account_number, current_balance
			from account_summary a
			where a.id in (select distinct t.account_summary_id from transaction t)
			order by a.account_number""")
			.rowMapper((rs, rowNum) -> {
				AccountSummary summary = new AccountSummary();
				summary.setAccountNumber(rs.getString("account_number"));
				summary.setCurrentBalance(rs.getDouble("current_balance"));
				return summary;
			})
			.build();
	}
	
	@Bean
	public TransactionDao transactionDao(DataSource dataSource) {
		return new TransactionDaoSupport(dataSource);
	}
	
	@Bean
	public TransactionApplierProcessor transactionApplierProcessor() {
		return new TransactionApplierProcessor(transactionDao(null));
	}
	
	@Bean
	public JdbcBatchItemWriter<AccountSummary> accountSummaryWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<AccountSummary>()
			.dataSource(dataSource)
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("""
				update account_summary
				set current_balance = :currentBalance
				where account_number = :accountNumber
			""")
			.build();
	}
	
	@Bean("stopTestConfiguration-apply-txStep")
	public Step applyTransactionStep() {
		return new StepBuilder("stopTestConfiguration-apply-txStep", jobRepository)
			.<AccountSummary, AccountSummary>chunk(10, transactionManager)
			.reader(accountSummaryReader(null))
			.processor(transactionApplierProcessor())
			.writer(accountSummaryWriter(null))
			.build();
	}
	
	@Bean("stopTestConfiguration-summaryfile-writer")
	@StepScope
	public FlatFileItemWriter<AccountSummary> accountSummaryFileWriter(
		@Value("#{jobParameters['summaryFile']}") FileSystemResource summaryFile
	) {
		DelimitedLineAggregator<AccountSummary> lineAggregator = new DelimitedLineAggregator<>();
		BeanWrapperFieldExtractor<AccountSummary> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[]{"accountNumber", "currentBalance"});
		fieldExtractor.afterPropertiesSet();
		lineAggregator.setFieldExtractor(fieldExtractor);
		
		return new FlatFileItemWriterBuilder<AccountSummary>()
			.name("stopTestConfiguration-summaryfile-writer")
			.resource(summaryFile)
			.lineAggregator(lineAggregator)
			.build();
	}
	
	@Bean("stopTestConfiguration-generate-summary-step")
	public Step generateAccountSummaryStep() {
		return new StepBuilder("stopTestConfiguration-generate-summary-step", jobRepository)
			.<AccountSummary, AccountSummary>chunk(10, transactionManager)
			.reader(accountSummaryReader(null))
			.writer(accountSummaryFileWriter(null))
			.build();
	}
	
	@Bean("stopTestConfiguration-tx-job")
	public Job transactionJob() {
		/*return new JobBuilder("stopTestConfiguration-tx-job", jobRepository)
			.start(importTransactionFileStep())
			.on("STOPPED").stopAndRestart(importTransactionFileStep())
			.from(importTransactionFileStep()).on("*").to(applyTransactionStep())
			.from(applyTransactionStep()).next(generateAccountSummaryStep())
			.end().build();*/
		return new JobBuilder("stopTestConfiguration-tx-job", jobRepository)
			.start(importTransactionFileStep())
			.next(applyTransactionStep())
			.next(generateAccountSummaryStep())
			.build();
	}
	
}
