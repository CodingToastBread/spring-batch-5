package coding.toast.batch.config.datasource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {
	
	@Bean
	@ConfigurationProperties("spring.datasource.global")
	public DataSourceProperties globalDataSourceProperties() {
		return new DataSourceProperties();
	}
	
	@Bean
	@Primary
	public DataSource dataSource() {
		return globalDataSourceProperties()
			.initializeDataSourceBuilder()
			.build();
	}
	
	@Bean
	@Primary
	public PlatformTransactionManager transactionManager() {
		return new JdbcTransactionManager(dataSource());
	}
	
}
