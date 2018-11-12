package io.mosip.authentication.core.dto.indauth;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 */

@Data
public class KycResponseDTO {
	
	private AuthResponseDTO auth;
	
	private KycInfo kyc;
}
