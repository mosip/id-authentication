package io.mosip.authentication.core.indauth.dto;

import lombok.Data;

/**
 * The class for IdentityKeyBindingRespDto Holds the values for IDA signed Identity Certificate 
 * and Auth Token (PSU Token).
 * 
 * @author Mahammed Taheer
 *
 *
 */

@Data
public class IdentityKeyBindingRespDto {

	/** The Variable to hold value of kyc Token */
	private String identityCertificate;

	/** The Variable to hold value of auth Token */
	private String authToken;

	/** The Variable to hold value of auth status */
	private boolean bindingAuthStatus;
}
