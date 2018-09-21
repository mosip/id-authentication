package org.mosip.registration.processor.dto.json.demo;

import java.util.Date;

import lombok.Data;

@Data
public class DemographicInfo {
	private String fullName;
	private Date dateOfBirth;
	private String gender;
	private Address address;
	private String emailId;
	private String mobile;
	private String languageCode;
	private boolean isChild;
}
