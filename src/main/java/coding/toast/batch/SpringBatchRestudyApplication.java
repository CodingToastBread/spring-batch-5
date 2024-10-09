package coding.toast.batch;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistrySmartInitializingSingleton;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBatchRestudyApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBatchRestudyApplication.class, args);
	}
	
	@Bean
	public static BeanDefinitionRegistryPostProcessor jobRegistryBeanPostProcessorRemover() {
		return registry -> registry.removeBeanDefinition("jobRegistryBeanPostProcessor");
	}
	
	@Bean
	public JobRegistrySmartInitializingSingleton jobRegistrySmartInitializingSingleton(JobRegistry jobRegistry) {
		return new JobRegistrySmartInitializingSingleton(jobRegistry);
	}
	
	@Bean
	public ExecutionContextSerializer executionContextSerializer() {
		return new Jackson2ExecutionContextStringSerializer();
	}
}
