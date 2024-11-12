package coding.toast.batch.job.reader.model;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class XmlCustomer {
	private String firstName;
	private String middleInitial;
	private String lastName;
	private String address;
	private String city;
	private String state;
	private String zipCode;
}
