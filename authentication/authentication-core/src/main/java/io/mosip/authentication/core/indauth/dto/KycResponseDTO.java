package io.mosip.authentication.core.indauth.dto;

import java.util.Map;

import lombok.Data;

/**
 * The class for KycResponseDTO Holds the values for ttl and Identity.
 * 
 * @author Prem Kumar
 *
 *
 */

@Data
public class KycResponseDTO {

	/** The Variable to hold value of kyc Status */
	private boolean kycStatus;

	/** The Variable to hold value of static Token */
	private String staticToken;

	/** The Variable to hold value of identity */
	private Map<String, ? extends Object> identity;
}
