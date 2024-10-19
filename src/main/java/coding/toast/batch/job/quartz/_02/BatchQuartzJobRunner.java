package coding.toast.batch.job.quartz._02;

import coding.toast.batch.job.quartz.BatchScheduledJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchQuartzJobRunner implements ApplicationRunner {
	
	private final JobParametersConverter converter = new DefaultJobParametersConverter();
	private final Scheduler scheduler;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		JobDetail jobDetail = quartzJobDetail();
		Trigger trigger = jobTrigger(jobDetail);
		
		// 여기서 들어오는 spring batch job 파라미터를 JobParameters 로 변환합니다.
		JobParameters jobParameter = createJobParameter(args);
		
		// Quartz 의 JobDataMap 에 JobParameters 넣고, 추후에
		// Quartz Job 인 BatchScheduledJob 에서 JobParameters 를 꺼내서 사용하게 된다.
		// 이렇게 해야 외부에서 들어온 JobParameters 를 Spring Batch Job 에 전송할 수 있다.
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		jobDataMap.put("jobParamFromArguments", jobParameter);
		scheduler.scheduleJob(jobDetail, trigger);
	}
	
	/**
	 * arguments 를 JobParameter 로 변환한다.
	 */
	private JobParameters createJobParameter(ApplicationArguments args) {
		String[] jobArguments = args.getNonOptionArgs().toArray(new String[0]);
		Properties properties = StringUtils.splitArrayElementsIntoProperties(jobArguments, "=");
		return converter.getJobParameters(properties);
	}
	
	/**
	 * Quartz 의 JobDetail 생성
	 */
	public JobDetail quartzJobDetail() {
		return org.quartz.JobBuilder
			.newJob(BatchScheduledJob.class)
			.storeDurably()
			.build();
	}
	
	/**
	 * JobTrigger 생성
	 */
	public Trigger jobTrigger(JobDetail jobDetail) {
		// 간단하게 하려면 simpleBuilder 를 사용하세요!
		SimpleScheduleBuilder simpleBuilder
			= SimpleScheduleBuilder.simpleSchedule()
			.withIntervalInSeconds(5)   // 매 5초마다
			.withRepeatCount(4);    // 최초 1회 이후 4번 반복 (= 총 5회 동작)
		
		return TriggerBuilder
			.newTrigger()
			.forJob(jobDetail)
			.withSchedule(simpleBuilder)
			.build();
	}
}
