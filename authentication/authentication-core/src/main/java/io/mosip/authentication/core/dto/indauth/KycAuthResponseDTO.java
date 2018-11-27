package io.mosip.authentication.core.dto.indauth;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 *
 */

@Data
public class KycAuthResponseDTO extends BaseAuthResponseDTO {
	private KycResponseDTO response;
	private String ttl;
}
