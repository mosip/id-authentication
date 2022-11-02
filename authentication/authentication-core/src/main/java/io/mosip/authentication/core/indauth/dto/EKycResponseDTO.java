package io.mosip.authentication.core.indauth.dto;

import lombok.Data;

/**
 * The class for KycResponseDTO Holds the values for ttl and Identity.
 * 
 * @author Prem Kumar
 *
 *
 */

@Data
public class EKycResponseDTO {

	/** The Variable to hold value of kyc Status */
	private boolean kycStatus;

	/** The Variable to hold value of auth Token */
	private String authToken;
	
	private String thumbprint;

	/** The Variable to hold value of identity */
	private String identity;
	
	/** The session key. */
	private String sessionKey;
}
