package io.mosip.kernel.auth.dto;

import lombok.Data;


/**
 * Instantiates a new keycloak error response dto.
 * @author srinivasan
 */
@Data
public class KeycloakErrorResponseDto {

	/** The error. */
	private String error;
	
	/** The error description. */
	private String error_description;
}
