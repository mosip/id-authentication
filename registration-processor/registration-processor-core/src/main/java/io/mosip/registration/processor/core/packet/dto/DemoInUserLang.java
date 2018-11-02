package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new demo in user lang.
 */
@Data
public class DemoInUserLang {

	/** The first name. */
	private String firstName;
	
	/** The forename. */
	private String forename;
	
	/** The givenname. */
	private String givenname;
	
	/** The middle name. */
	private String middleName;
	
	/** The middleinitial. */
	private String middleinitial;
	
	/** The last name. */
	private String lastName;
	
	/** The surname. */
	private String surname;
	
	/** The familyname. */
	private String familyname;
	
	/** The full name. */
	private String fullName;
	
	/** The date of birth. */
	private String dateOfBirth;
	
	/** The gender. */
	private String gender;
	
	/** The address DTO. */
	private AddressDTO addressDTO;
	
	/** The email id. */
	private String emailId;
	
	/** The mobile. */
	private String mobile;
	
	/** The language code. */
	private String languageCode;
	
	/** The child. */
	private Boolean child;

}