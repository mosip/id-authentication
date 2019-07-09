package io.mosip.registrationProcessor.perf.dto;

import lombok.Data;

@Data
public class RegDataCSVDto {

	private String fullName;

	private String dateOfBirth;

	private String age;

	private String gender;

	private String residenceStatus;

	private String addressLine1;

	private String addressLine2;

	private String addressLine3;

	private String region;

	private String province;

	private String city;

	private String postalCode;

	private String phone;

	private String email;

	private String localAdministrativeAuthority;
	
	private String cnieNumber;
}
