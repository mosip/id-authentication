package io.mosip.authentication.core.indauth.dto;

import lombok.Data;

/**
 * The class for KycAuthRespDTO Holds the values for Kyc Token and Auth Token (PSU Token).
 * 
 * @author Mahammed Taheer
 *
 *
 */

@Data
public class KycAuthRespDTO {

	/** The Variable to hold value of kyc Token */
	private String kycToken;

	/** The Variable to hold value of auth Token */
	private String authToken;

	/** The Variable to hold value of auth status */
	private boolean kycStatus;
}
