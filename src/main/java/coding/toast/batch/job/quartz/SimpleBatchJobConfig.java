package coding.toast.batch.job.quartz;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SimpleBatchJobConfig {
	
	private final JobRepository jobRepository;
	private final PlatformTransactionManager txManager;
	
	@Bean
	public Job quartzJobConfigJob() {
		return new JobBuilder("quartzJobConfigJob", jobRepository)
			.start(quartzJobConfigStep())
			.incrementer(new RunIdIncrementer())
			.build();
	}
	
	@Bean
	public Step quartzJobConfigStep() {
		return new StepBuilder("quartzJobConfigStep", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				System.out.println("quartzJobConfigStep RAN!");
				return RepeatStatus.FINISHED;
			}, txManager)
			.build()
			;
	}
}
