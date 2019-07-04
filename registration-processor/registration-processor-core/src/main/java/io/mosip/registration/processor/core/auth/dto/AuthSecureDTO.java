package io.mosip.registration.processor.core.auth.dto;

import lombok.Data;


/**
 * The Class AuthSecureDTO.
 *
 * @author Arun Bose
 */

/**
 * Instantiates a new auth secure DTO.
 */
@Data
public class AuthSecureDTO {
	
	/** variable to hold session key. */
	private String sessionKey;
	
	/** variable for publickey certificate. */
	private String publicKeyCert;

}
