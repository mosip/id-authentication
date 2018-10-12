package io.mosip.registration.dto.demographic;

import java.util.Date;

import io.mosip.registration.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class used to capture the Demographic details of the Individual
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DemographicInfoDTO extends BaseDTO {

	private String firstName;
	private String forename;
	private String givenName;
	private String middleName;
	private String middleInitial;
	private String lastName;
	private String surname;
	private String familyName;
	private String fullName;
	protected Date dateOfBirth;
	protected String gender;
	protected AddressDTO addressDTO;
	protected String emailId;
	protected String mobile;
	protected String languageCode;
	protected boolean isChild;
}
