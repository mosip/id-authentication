package io.mosip.authentication.core.indauth.dto;

import lombok.Data;

/**
 * The Auth Response Info class.
 * 
 * @author Rakesh Roshan
 */
@Data
public class AuthResponseInfo {

	
	/**
	 * Type of user ID ("D" or "V") as per the {@link IdType}
	 */
	private String idType;
	
	/**
	 * Request Time
	 */
	private String reqTime;
	
	/**
	 * The 16 digit Hexa decimal data that encodes the authentication types that are 
	 * used and the authentication types that are matched.
	 */
	private String usageData;
	


}
