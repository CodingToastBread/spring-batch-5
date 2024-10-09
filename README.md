# Spring Batch 5 Study Repository

> 프로젝트 참고사항:
> - `spring boot 3.3.4` 사용 (=> `spring batch 5.1.x` 사용)
> - `java`: `temurin-21 jdk` 사용
> - `db`: `postgres-16` 사용

<br>

## 1. 기본 설정법

**- Spring Config (Java)**

```java
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
	
	// removing problematic jobRegistryBeanPostProcessor
	@Bean
	public static BeanDefinitionRegistryPostProcessor jobRegistryBeanPostProcessorRemover() {
		return registry -> registry.removeBeanDefinition("jobRegistryBeanPostProcessor");
	}
	
	// alternative for jobRegistryBeanPostProcessor
	@Bean
	public JobRegistrySmartInitializingSingleton jobRegistrySmartInitializingSingleton(JobRegistry jobRegistry) {
		return new JobRegistrySmartInitializingSingleton(jobRegistry);
	}
	
	// use Jackson for Execution Context serialize. this will use json format.
	@Bean
	public ExecutionContextSerializer executionContextSerializer() {
		return new Jackson2ExecutionContextStringSerializer();
	}
}
```

<br>

**- Spring Config (Yaml)**

```yaml
spring:
  application:
    name: spring-batch-restudy
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: postgres
  batch:
      jdbc:
        initialize-schema: always
        platform: postgresql
      job:
        enabled: false
```

- `spring.batch.job.enabled: false` : 자동실행 방지
- `spring.batch.jdbc.initialize-schema: always` : 배치 프레임워크 내부에서 사용되는 테이블 자동 설치
- `spring.batch.jdbc.platform: postgresql` : 프레임워크 테이블 설치시 사용할 `schema-???.sql` 결정
  - 더 정확히는 `spring-batch-core-?.?.?.jar` 안에 있는 `schema-postgresql.sql` 사용

