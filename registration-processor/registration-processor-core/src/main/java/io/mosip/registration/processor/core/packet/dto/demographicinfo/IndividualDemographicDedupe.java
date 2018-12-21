package io.mosip.registration.processor.core.packet.dto.demographicinfo;

import java.util.List;

import lombok.Data;

@Data
public class IndividualDemographicDedupe {
	private List<JsonValue[]> name;

	private JsonValue[] dateOfBirth;

	private JsonValue[] gender;
	
	private String pheoniticName;




}
