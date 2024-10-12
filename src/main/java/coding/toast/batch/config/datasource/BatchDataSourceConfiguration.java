package coding.toast.batch.config.datasource;

import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.batch.BatchTransactionManager;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchDataSourceConfiguration {
	
	@Bean
	@ConfigurationProperties("spring.datasource.batch")
	public DataSourceProperties batchDataSourceProperties() {
		return new DataSourceProperties();
	}
	
	@Bean
	@BatchDataSource
	public DataSource batchDataSource() {
		return batchDataSourceProperties().initializeDataSourceBuilder()
			.build();
	}
	
	@Bean
	@BatchTransactionManager
	public PlatformTransactionManager batchTransactionManager() {
		return new JdbcTransactionManager(batchDataSource());
	}
}
