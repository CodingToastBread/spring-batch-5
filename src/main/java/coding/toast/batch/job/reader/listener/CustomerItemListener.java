package coding.toast.batch.job.reader.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.batch.item.file.FlatFileParseException;

public class CustomerItemListener {
	private static final Logger log = LoggerFactory.getLogger(CustomerItemListener.class);
	
	@OnReadError
	public void onReadError(Exception e) {
		if (e instanceof FlatFileParseException ffpe) {
			log.error("""
				An error occurred while processing the {} line of the file
				Below was the faulty input
				==> {}
				""", ffpe.getLineNumber(), ffpe.getInput());
		} else {
			log.error("An Error has occurred", e);
		}
	}
}
