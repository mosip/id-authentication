package io.mosip.authentication.core.dto.indauth;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 *The class for KycResponseDTO
 */

@Data
public class KycResponseDTO {
	/** The AuthResponseDTO auth */
	private AuthResponseDTO auth;
	
	/** The KycInfo kyc*/
	private KycInfo kyc;
}
