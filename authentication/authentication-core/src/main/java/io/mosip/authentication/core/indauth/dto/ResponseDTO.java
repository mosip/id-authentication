package io.mosip.authentication.core.indauth.dto;

import lombok.Data;

/**
 * This class provides the details of Status and StaticToken Details.
 * 
 * @author Prem Kumar
 *
 */
@Data
public class ResponseDTO {
	
	/** The boolean value for status */
	private boolean authStatus;
	
	/** Static token. */
	private String staticToken;
}