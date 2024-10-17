package coding.toast.batch.job.quartz;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
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
public class QuartzJobConfig {
	
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
	
	@Bean
	public JobDetail quartzJobDetail() {
		return org.quartz.JobBuilder.newJob(BatchScheduledJob.class)
			.storeDurably()
			.build();
	}
	
	@Bean
	public Trigger jobTrigger() {
		
		// 간단하게 하려면 simpleBuilder 를 사용하세요!
		SimpleScheduleBuilder simpleBuilder
			= SimpleScheduleBuilder.simpleSchedule()
					.withIntervalInSeconds(5)   // 매 5초마다
					.withRepeatCount(4);    // 최초 1회 이후 4번 반복 (= 총 5회 동작)
		
		// cron 이 편하신 분들은 아래 방법 사용.
		/* CronScheduleBuilder cronBuilder
			= CronScheduleBuilder.cronSchedule("0/5 * * * * ?");*/ // 매 5초마다.
		
		return TriggerBuilder
			.newTrigger()
			.forJob(quartzJobDetail())
			.withSchedule(simpleBuilder)
			// .withSchedule(cronBuilder)
			.build();
	}
	
}
