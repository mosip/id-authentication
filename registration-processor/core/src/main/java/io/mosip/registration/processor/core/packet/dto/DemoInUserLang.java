package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class DemoInUserLang {

	private String firstName;
	private String forename;
	private String givenname;
	private String middleName;
	private String middleinitial;
	private String lastName;
	private String surname;
	private String familyname;
	private String fullName;
	private String dateOfBirth;
	private String gender;
	private AddressDTO addressDTO;
	private String emailId;
	private String mobile;
	private String languageCode;
	private Boolean child;

}