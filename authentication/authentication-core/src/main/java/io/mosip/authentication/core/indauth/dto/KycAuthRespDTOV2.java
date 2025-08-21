package io.mosip.authentication.core.indauth.dto;

import lombok.Data;

/**
 * The class for KycAuthRespDTOV2 Holds the values for Kyc Token and Auth Token (PSU Token).
 * 
 * @author Mahammed Taheer
 */

@Data
public class KycAuthRespDTOV2 extends KycAuthRespDTO{

	/** The Variable to hold value of verified claims */
	private String verifiedClaimsMetadata;
}
