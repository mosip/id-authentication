package io.mosip.authentication.service.policy;

import lombok.Data;

/**
 * The Class AuthTypeAttribute which has attributes for  authType(authentication type) allowed.
 * @author Arun Bose S 
 */


@Data
public class AuthTypeAttribute {

	/**  authentication type used. */
	private String authType;
	
	/**  mandatory attribute value */
	private boolean mandatory;
}
