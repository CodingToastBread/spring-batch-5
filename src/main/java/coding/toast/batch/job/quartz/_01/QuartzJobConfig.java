package coding.toast.batch.job.quartz._01;

import coding.toast.batch.job.quartz.BatchScheduledJob;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration
public class QuartzJobConfig {
	
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
