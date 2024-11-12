package coding.toast.batch.job.reader.skip;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.lang.NonNull;

import java.io.FileNotFoundException;
import java.text.ParseException;

public class FileVerificationSkipper implements SkipPolicy {
	@Override
	public boolean shouldSkip(@NonNull Throwable t, long skipCount) throws SkipLimitExceededException {
		return switch (t) {
			case FileNotFoundException _ -> false;
			case ParseException _ when skipCount <= 10 -> true;
			default -> false;
		};
	}
}
