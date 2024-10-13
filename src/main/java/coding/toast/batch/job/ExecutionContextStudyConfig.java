package coding.toast.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ExecutionContextStudyConfig {
	
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	
	@Bean("ExecutionContextStudyConfig-Job")
	public Job simpleJob() {
		return new JobBuilder("ExecutionContextStudyConfig-Job", jobRepository)
			.start(simpleStep())
			.incrementer(new RunIdIncrementer())
			.listener(new JobLoggerListener())
			.build();
	}

	@Bean("ExecutionContextStudyConfig-step")
	public Step simpleStep() {
		return new StepBuilder("ExecutionContextStudyConfig-step", jobRepository)
			.tasklet(simpleTasklet(), transactionManager)
			.listener(promotionListener())
			.build();
	}
	
	@Bean("ExecutionContextStudyConfig-tasklet")
	public Tasklet simpleTasklet() {
		return (StepContribution contribution, ChunkContext chunkContext) -> {
			
			StepExecution stepExecution = chunkContext
				.getStepContext().getStepExecution();
			
			ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
			
			ExecutionContext jobExecutionContext = stepExecution
				.getJobExecution().getExecutionContext();
			
			jobExecutionContext.put("job.version", 1);
			stepExecutionContext.put("step.version", 2);
			stepExecutionContext.
				put("promote.value", "this value will be set on step and job execution context at same time");
			
			System.out.println("Tasklet Start");
			return RepeatStatus.FINISHED;
		};
	}
	
	public static class JobLoggerListener implements JobExecutionListener {
		private final static String START_MESSAGE = "job [%s] START!%n";
		private final static String END_MESSAGE = "job [%s] END! Batch STATUS : [%s]%n";
		
		@Override
		public void beforeJob(@NonNull JobExecution jobExecution) {
			System.out.printf(START_MESSAGE, jobExecution.getJobInstance().getJobName());
		}
		
		@Override
		public void afterJob(@NonNull JobExecution jobExecution) {
			System.out.printf(END_MESSAGE,
				jobExecution.getJobInstance().getJobName(),
				jobExecution.getStatus().name());
		}
	}
	
	@Bean
	public StepExecutionListener promotionListener() {
		ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
		listener.setKeys(new String[]{"promote.value"});
		return listener;
	}
}
