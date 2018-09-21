package org.mosip.registration.processor.dto.demographic;

import java.util.Date;

import lombok.Data;

@Data
public class DemographicInfoDTO {
	
	private String fullName;
	private Date dateOfBirth;
	private String gender;
	private AddressDTO addressDTO;
	private String emailId;
	private String mobile;
	private String languageCode;
	private boolean isChild;

}
