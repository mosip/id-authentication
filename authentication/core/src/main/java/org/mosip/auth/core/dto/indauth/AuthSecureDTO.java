package org.mosip.auth.core.dto.indauth;

import lombok.Data;

/**
 * 
 * @author Arun Bose
 */

@Data
public class AuthSecureDTO {
	
	private String sessionKey;
	
	private String publicKeyCert;

}
