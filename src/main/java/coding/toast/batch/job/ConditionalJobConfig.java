package coding.toast.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ConditionalJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job conditionalJobConfigJob() {
        return new JobBuilder("conditionalJobConfigJob", jobRepository)
                .start(startingStep())
                .on("FAILED").stopAndRestart(successStep())
                .from(startingStep()).on("*")
                .to(successStep())
                .end()
                .build();
    }

    @Bean("conditionalJobConfig-startingStep")
    public Step startingStep() {
        return new StepBuilder("conditionalJobConfig-startingStep", jobRepository)
                .tasklet(startingTasklet(), transactionManager)
                .build();
    }


    @Bean("conditionalJobConfig-successStep")
    public Step successStep() {
        return new StepBuilder("conditionalJobConfig-successStep", jobRepository)
                .tasklet(successTasklet(), transactionManager)
                .build();
    }

    @Bean("conditionalJobConfig-failedStep")
    public Step failedStep() {
        return new StepBuilder("conditionalJobConfig-failedStep", jobRepository)
                .tasklet(failTasklet(), transactionManager)
                .build();
    }

    private Tasklet startingTasklet() {
        return (contribution, chunkContext) -> {
//            return RepeatStatus.FINISHED;
            System.out.println("STARTING TASKLET!");
             throw new RuntimeException("Starting Tasklet Threw ERROR!");
        };
    }

    private Tasklet successTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("SUCCESS TASKLET!");
            return RepeatStatus.FINISHED;
        };
    }


    private Tasklet failTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("FAILED TASKLET");
            throw new RuntimeException("Failed Tasklet!");
        };
    }
}
