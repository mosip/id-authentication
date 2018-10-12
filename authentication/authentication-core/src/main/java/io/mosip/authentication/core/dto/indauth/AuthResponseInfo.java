package io.mosip.authentication.core.dto.indauth;

import java.util.Date;
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
	 * Static token
	 */
	//TODO
	private String staticToken;
	
	/**
	 * Type of user ID ("D" or "V") as per the {@link IdType}
	 */
	private String idType;
	/**
	 * Request Time
	 */
	private Date reqTime;
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

}
