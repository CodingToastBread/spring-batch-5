package coding.toast.batch.job.quartz;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class BatchScheduledJob extends QuartzJobBean {
	private final Job job;
	private final JobExplorer jobExplorer;
	private final JobLauncher jobLauncher;
	
	public BatchScheduledJob(@Qualifier("quartzJobConfigJob") Job job,
	                         JobExplorer jobExplorer,
	                         JobLauncher jobLauncher) {
		this.job = job;
		this.jobExplorer = jobExplorer;
		this.jobLauncher = jobLauncher;
	}
	
	@Override
	protected void executeInternal(@NonNull JobExecutionContext context) {
		JobDataMap dataMap = context.getMergedJobDataMap();
		
		JobParameters jobParameters = new JobParametersBuilder(this.jobExplorer)
			.getNextJobParameters(this.job)
			.addJobParameters((JobParameters)dataMap.get("jobParamFromArguments"))
			.toJobParameters();
		
		try {
			this.jobLauncher.run(this.job, jobParameters);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
