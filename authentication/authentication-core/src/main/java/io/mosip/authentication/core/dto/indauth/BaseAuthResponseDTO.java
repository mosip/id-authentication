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
	
	/** The Status value */
	private String status;
	
	/** The error List */
	private List<AuthError> err;
	
	/** The txnID value*/
	private String txnID;
	
	/** The resTime value */
	private String resTime;
}
