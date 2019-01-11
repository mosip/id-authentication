package io.mosip.registration.processor.core.auth.dto;

import java.util.List;

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
	 * Version
	 */
	//TODO
	private String ver;
	
	/**
	 * List of all match informations used in different authentication types such as
	 * Demo Auth, Bio Auth, etc...
	 */
	private List<MatchInfo> matchInfos;
	
	/**
	 * The 16 digit Hexa decimal data that encodes the authentication types that are 
	 * used and the authentication types that are matched.
	 */
	private String usageData;
	
//	/**
//	 * Static token
//	 */
//	//TODO
//	private String staticToken;

}
