package coding.toast.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class JobParameterStudyConfig {
	
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	
	/*
	java -jar target/batch.jar \
	taskVersion=1,java.lang.Long \
	taskManagerId=1,java.lang.Integer \
	taskName=XmlToDatabase,java.lang.String \
	taskSummary="transfer data to database",java.lang.String,false \
	executDate=2024-10-12,java.time.LocalDate \
	executeTimeStamp=2011-12-03T10:15:30,java.time.LocalDateTime
	 */
	@Bean("JobParameterStudyConfig-Job")
	public Job simpleJob() {
		return new JobBuilder("JobParameterStudyConfig-Job", jobRepository)
			.start(simpleStep())
			.validator(validator())
			.incrementer(new RunIdIncrementer())
			.build();
	}
	
	@Bean("JobParameterStudyConfig-validator")
	public CompositeJobParametersValidator validator() {
		CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
		DefaultJobParametersValidator defaultValidator = new DefaultJobParametersValidator(
			new String[]{"taskVersion"}, // 필수값인 파라미터
			new String[]{}// 필수값이 아닌 파라미터
		);
		
		validator.setValidators(List.of(defaultValidator, new MyCustomValidator()));
		return validator;
	}

	public static class MyCustomValidator implements JobParametersValidator {
		@Override
		public void validate(JobParameters parameters) throws JobParametersInvalidException {
			if (parameters != null) {
				Long taskVersion = parameters.getLong("taskVersion", -1L);
				if (taskVersion == null || taskVersion <= 0) {
					throw new JobParametersInvalidException("taskVersion must be higher than Zero");
				}
			}
		}
	}
	
	@Bean("JobParameterStudyConfig-step")
	public Step simpleStep() {
		return new StepBuilder("JobParameterStudyConfig-step", jobRepository)
			.tasklet(simpleTasklet(null), transactionManager)
			.build();
	}
	
	@Bean("JobParameterStudyConfig-tasklet")
	@StepScope
	public Tasklet simpleTasklet(@Value("#{jobParameters['taskSummary']}") String taskSummary) {
		return (contribution, chunkContext) -> {
			System.out.println("taskSummary = " + taskSummary);
			return RepeatStatus.FINISHED;
		};
	}
}
