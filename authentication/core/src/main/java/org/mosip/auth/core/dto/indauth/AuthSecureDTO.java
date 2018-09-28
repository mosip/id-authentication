package org.mosip.auth.core.dto.indauth;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * @author Arun Bose
 */

@Data
public class AuthSecureDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sessionKey;
	
	private String publicKeyCert;

}
