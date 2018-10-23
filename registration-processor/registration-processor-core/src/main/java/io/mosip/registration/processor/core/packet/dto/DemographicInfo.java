package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class DemographicInfo {

	private String firstName;
	private String middleName;
	private String lastName;
	private String fullName;
	private String dateOfBirth;
	private String gender;
	private AddressDTO addressDTO;
	private String emailId;
	private String mobile;
	private String landLine;
	private String languageCode;
	private String age;
	private Boolean child;

}
