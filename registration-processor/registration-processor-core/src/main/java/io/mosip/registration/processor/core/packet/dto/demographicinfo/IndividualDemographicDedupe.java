package io.mosip.registration.processor.core.packet.dto.demographicinfo;
	
import java.util.Arrays;

import lombok.Data;

/**
 * Instantiates a new individual demographic dedupe.
 */
@Data
public class IndividualDemographicDedupe {
	
	/** The name. */
	private JsonValue[] name;

	/** The date of birth. */
	private String dateOfBirth;

	public JsonValue[] getName() {
		return Arrays.copyOf(name,name.length);
	}

	public void setName(JsonValue[] name) {
		this.name = name!=null?name:null;
	}

	public JsonValue[] getGender() {
		return Arrays.copyOf(gender,gender.length);
	}

	public void setGender(JsonValue[] gender) {
		this.gender = gender!=null?gender:null;
	}

	/** The gender. */
	private JsonValue[] gender;
	
	/** The pheonitic name. */
	private String pheoniticName;




}
