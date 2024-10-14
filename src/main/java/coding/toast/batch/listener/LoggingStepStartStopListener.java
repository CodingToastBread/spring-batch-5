package coding.toast.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;

public class LoggingStepStartStopListener {
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("StepName = " + stepExecution.getStepName() + " will start soon...");
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("StepName = " + stepExecution.getStepName() + " is done!");
        return stepExecution.getExitStatus();
    }

    @BeforeChunk
    public void beforeChunk(ChunkContext context) {
        System.out.println("chunk will start soon");
    }

    @AfterChunk
    public void afterChunk(ChunkContext context) {
        System.out.println("chunk end");
    }
}
