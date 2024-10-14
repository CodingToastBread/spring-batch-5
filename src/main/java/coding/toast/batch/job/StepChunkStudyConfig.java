package coding.toast.batch.job;

import coding.toast.batch.listener.LoggingStepStartStopListener;
import coding.toast.batch.policy.RandomChunkSizePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class StepChunkStudyConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job stepChunkStudyConfigJob() {
        return new JobBuilder("stepChunkStudyConfigJob", jobRepository)
                .start(stepChunkStudyConfigStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step stepChunkStudyConfigStep() {
        return new StepBuilder("stepChunkStudyConfigStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
//                .<String, String>chunk(randomCompletionPolicy(), transactionManager)
                .reader(itemReader())
                .writer(itemWriter())
                .listener(new LoggingStepStartStopListener())
                .build();
    }

    @Bean("stepChunkStudyConfigItemReader")
    public ItemReader<String> itemReader() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(UUID.randomUUID().toString());
        }
        return new ListItemReader<>(list);
    }

    @Bean("stepChunkStudyConfigItemWriter")
    public ItemWriter<String> itemWriter() {
        return chunk -> {
            System.out.println("CHUNK WRITER CALL [START]");
            chunk.getItems().forEach(System.out::println);
            System.out.println("CHUNK WRITER CALL [END]");
        };
    }

    @Bean("stepChunkStudyConfigCompletePolicy")
    public CompletionPolicy randomCompletionPolicy() {
        return new RandomChunkSizePolicy();
    }
}
