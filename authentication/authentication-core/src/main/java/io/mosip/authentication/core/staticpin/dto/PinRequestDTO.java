package io.mosip.authentication.core.staticpin.dto;

import lombok.Data;

/**
 * This Class Provides the Values of StaticPin and StaticPinIdentityDTO
 * 
 * @author Prem Kumar
 *
 */
@Data
public class PinRequestDTO {

	/** The value pinValue */
	private String staticPin;
}
