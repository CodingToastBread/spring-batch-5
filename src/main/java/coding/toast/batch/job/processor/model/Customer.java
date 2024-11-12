package coding.toast.batch.job.processor.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Customer {
	@NotNull(message = "First name is required")
	@Pattern(regexp = "[a-zA-Z]+", message = "First name must be alphabetical")
	private String firstNAme;
	
	@Size(min = 1, max = 1)
	@Pattern(regexp = "[a-zA-Z]+", message = "Middle Initial name must be alphabetical")
	private String middleInitial;

	@NotNull(message = "Last name is required")
	@Pattern(regexp = "[a-zA-Z]+", message = "Last name must be alphabetical")
	private String lastName;
	
	@NotNull(message = "Address is required")
	@Pattern(regexp = "[0-9a-zA-Z. ]+")
	private String address;
	
	@NotNull(message = "City is required")
	@Pattern(regexp = "[0-9a-zA-Z. ]+")
	private String city;
	
	@NotNull(message = "City is required")
	@Size(min = 2, max = 2)
	@Pattern(regexp = "[A-Z]{2}")
	private String state;
	
	@NotNull(message = "Zip is required")
	@Size(min = 5, max = 5)
	@Pattern(regexp = "\\d{5}")
	private String zip;
}
