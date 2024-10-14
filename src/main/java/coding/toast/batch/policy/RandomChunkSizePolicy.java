package coding.toast.batch.policy;

import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.context.RepeatContextSupport;
import org.springframework.lang.NonNull;

import java.security.SecureRandom;

public class RandomChunkSizePolicy implements CompletionPolicy {

    private int chunkSize;
    private int totalProcessed;
    private final SecureRandom random = new SecureRandom();

    @Override
    public boolean isComplete(@NonNull RepeatContext context,@NonNull RepeatStatus result) {
        if (RepeatStatus.FINISHED == result) {
            return true;
        } else {
            return isComplete(context);
        }
    }

    @Override
    public boolean isComplete(@NonNull RepeatContext context) {
        return this.totalProcessed >= chunkSize;
    }

    @Override
    @NonNull
    public RepeatContext start(@NonNull RepeatContext parent) {
        this.chunkSize = random.nextInt(20);
        this.totalProcessed = 0;

        System.out.println("Create New RandomChunkSizePolicy with Chunksize => " + this.chunkSize);
        return parent;
    }

    @Override
    public void update(@NonNull RepeatContext context) {
        this.totalProcessed++;
    }
}
