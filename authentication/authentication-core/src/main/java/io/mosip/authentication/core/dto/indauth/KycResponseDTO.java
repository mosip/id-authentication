package io.mosip.authentication.core.dto.indauth;

import java.util.Map;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 *The class for KycResponseDTO
 */

@Data
public class KycResponseDTO {
	
	private String ttl;
	private Map<String, ? extends Object> identity;
}
