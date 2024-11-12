package coding.toast.batch.job.reader.listener;

import org.springframework.batch.core.*;

public class EmptyReadFailureListener implements StepExecutionListener {
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// 기존에 이미 예외가 터져서 failed 된 상태인지 먼저 확인한다.
		if (!stepExecution.getStatus().equals(BatchStatus.FAILED)) {
			if (stepExecution.getReadCount() > 0) {
				return stepExecution.getExitStatus();
			} else {
				// stepExecution.setStatus(BatchStatus.FAILED); // For Batch Restart, and also prevent next step execution
				// return ExitStatus.FAILED.addExitDescription("exit Because Of No Item Read");
				stepExecution.setStatus(BatchStatus.FAILED);
				return new ExitStatus("EMPTY_READ")
					.addExitDescription("exit Because Of No Item Read");
			}
		}
		return stepExecution.getExitStatus();
	}
}
