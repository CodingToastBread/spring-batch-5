package coding.toast.batch.job.reader.tokenizer;

import org.springframework.batch.item.file.transform.DefaultFieldSetFactory;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FieldSetFactory;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CustomerFileLineTokenizer implements LineTokenizer {
	
	private final String delimiter = ",";
	private final String[] names = new String[]{
		"firstName",
		"middleInitial",
		"lastName",
		"address",
		"city",
		"state",
		"zipCode"
	};
	
	private final FieldSetFactory fieldSetFactory = new DefaultFieldSetFactory();
	
	@Override
	@NonNull
	public FieldSet tokenize(String line) {
		String[] fields = line.split(delimiter, -1);
		List<String> parsedFields = new ArrayList<>();
		for (int i = 0; i < fields.length; i++) {
			if (i == 4) {
				parsedFields.set(i - 1,
					parsedFields.get(i - 1) + " " + fields[i]);
			} else {
				parsedFields.add(fields[i]);
			}
		}
		return fieldSetFactory.create(parsedFields.toArray(new String[0]), names);
	}
}
