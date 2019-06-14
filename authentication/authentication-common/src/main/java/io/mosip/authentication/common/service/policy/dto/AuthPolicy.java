package io.mosip.authentication.common.service.policy.dto;

import lombok.Data;

/**
 * The Class AuthTypeAttribute which has attributes for  authType(authentication type) allowed in auth policy json.
 * @author Arun Bose S 
 */


@Data
public class AuthPolicy{

	/**  authentication type used. */
	private String authType;
	
	/**  authentication subType used. */
	private String authSubType;
	
	/**  mandatory attribute value */
	private boolean mandatory;
}
