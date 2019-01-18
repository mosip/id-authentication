package io.mosip.authentication.core.dto.indauth;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 *The Class For KycAuthResponseDTO extending BaseAuthResponseDTO
 */

@Data
public class KycAuthResponseDTO extends BaseAuthResponseDTO {
	
	/** The KycResponseDTO */
	private KycResponseDTO response;
	
	/** The String value for ttl */
	private String ttl;
}
