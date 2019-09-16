package io.mosip.authentication.core.authtype.dto;

import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */
@Data
public class AuthtypeStatus {

	String authType;
	String authSubType;
	Boolean locked;

}
