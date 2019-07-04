package io.mosip.authentication.core.indauth.dto;

import java.util.List;

import lombok.Data;

/**
 * The Class for BaseAuthResponseDTO
 * 
 * @author Prem Kumar
 *
 *
 */

@Data
public class BaseAuthResponseDTO {

	/** The txnID value. */
	private String transactionID;

	/** Version. */
	private String version;

	/** The id. */
	private String id;
	
	/** The error List */
	private List<AuthError> errors;
	
	/** The resTime value */
	private String responseTime;
}
