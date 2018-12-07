package io.mosip.registration.dto.mastersync;

import java.util.List;

/**
 * Response DTO for fetching gender Data
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

public class GenderTypeResponseDto {
	
	private List<GenderTypeDto> gender;

	/**
	 * @return the gender
	 */
	public List<GenderTypeDto> getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(List<GenderTypeDto> gender) {
		this.gender = gender;
	}
	
	

}
