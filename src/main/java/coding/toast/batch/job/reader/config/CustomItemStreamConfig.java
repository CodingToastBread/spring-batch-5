package coding.toast.batch.job.reader.config;

import coding.toast.batch.job.reader.listener.EmptyReadFailureListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CustomItemStreamConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	
	@Bean
	public Job customItemStreamConfigJob() {
		//@formatter:off
		/*return new JobBuilder("customItemStreamConfigJob", jobRepository)
			.start(customItemStreamConfigStep())
			.next(simpleStep())
			.build();*/
		/*return new JobBuilder("customItemStreamConfigJob", jobRepository)
			.start(customItemStreamConfigStep())
				.on("EMPTY_READ")
				.fail()
			.from(customItemStreamConfigStep())
				.on("*").to(simpleStep())
				.end()
			.build();*/
		return new JobBuilder("customItemStreamConfigJob", jobRepository)
			.start(customItemStreamConfigStep())
			.on("EMPTY_READ").fail()
			.next(simpleStep()).end()
			.build();
		//@formatter:on
	}
	
	private Step simpleStep() {
		return new StepBuilder("simpleStep", jobRepository)
			.tasklet((_, _) -> {
				System.out.println("I AM STEP1");
				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}
	
	@Bean
	public Step customItemStreamConfigStep() {
		return new StepBuilder("customItemStreamConfigStep", jobRepository)
			.<Integer, Integer>chunk(5, transactionManager)
			.reader(new ListItemReader<>(List.of(1,2,3,4,5,6,7))) // 고의적으로 비워놨습니다!
			.writer(chunk -> chunk.getItems().forEach(System.out::println))
			.listener(emptyReadFailureListener())
			.build();
	}
	
	@Bean
	public EmptyReadFailureListener emptyReadFailureListener() {
		return new EmptyReadFailureListener();
	}
}
