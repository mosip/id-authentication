package io.mosip.authentication.core.indauth.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
/**
 * This class is a generic for the static pin and otp
 * @author Arun Bose
 */

@Data
public class PinDTO {

	/**
	 * variable to hold pin value
	 */
	@NotNull
	private String value;
	
	/**
	 * variable to hold pin type
	 */
	@NotNull
	private PinType type;
}
