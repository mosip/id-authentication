package io.mosip.registration.processor.core.packet.dto.demographicinfo;

import lombok.Data;

@Data
public class IndividualDemographicDedupe {
	private JsonValue[] firstName;

	private JsonValue[] middleName;

	private JsonValue[] lastName;

	private JsonValue[] fullName;

	private JsonValue[] dateOfBirth;

	private JsonValue[] gender;

	private JsonValue[] addressLine1;

	private JsonValue[] addressLine2;

	private JsonValue[] addressLine3;

	private JsonValue[] addressLine4;

	private JsonValue[] addressLine5;

	private JsonValue[] addressLine6;

	private JsonValue[] zipcode;


}
