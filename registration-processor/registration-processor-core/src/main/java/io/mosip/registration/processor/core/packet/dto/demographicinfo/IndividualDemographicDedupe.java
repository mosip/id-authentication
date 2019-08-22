package io.mosip.registration.processor.core.packet.dto.demographicinfo;
	
import java.util.Arrays;

import io.mosip.registration.processor.core.packet.dto.abis.Analytics;
import lombok.Data;

/**
 * Instantiates a new individual demographic dedupe.
 */
@Data
public class IndividualDemographicDedupe {
	
	/** The name. */
	private JsonValue[] name;
	public JsonValue[] getName() {
		return name.clone();
	}

	public void setName(JsonValue[] name) {
		this.name = name!=null?name:null;
	}

	/** The date of birth. */
	private String dateOfBirth;

	/** The gender. */
	private JsonValue[] gender;
	public JsonValue[] getGender() {
		return gender != null ? gender.clone() : null;
	}

	public void setGender(JsonValue[] gender) {
		this.gender = gender!=null?gender:null;
	}
	
	/** The pheonitic name. */
	private String pheoniticName;




}
