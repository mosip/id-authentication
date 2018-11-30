package io.mosip.registration.processor.manual.adjudication.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DemographicDto {
	private String fullName;
	private String dateOfBirth;
	private String gender;
	private String addressLine1;
	private String city;
	private String province;
	private String postalCode;
	private String mobile;
	private String email;
}
