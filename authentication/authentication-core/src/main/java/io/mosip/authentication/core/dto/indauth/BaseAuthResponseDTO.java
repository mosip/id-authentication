package io.mosip.authentication.core.dto.indauth;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 *The Class for BaseAuthResponseDTO
 */

@Data
public class BaseAuthResponseDTO {
	
	/** The boolean value for status */
	private boolean status;
	
	/** The error List */
	private List<AuthError> errors;
	
	/** The resTime value */
	private String responseTime;
}
