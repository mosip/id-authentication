package io.mosip.authentication.core.dto.indauth;

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
	
	private String ttl;
	private Map<String, ? extends Object> identity;
}
