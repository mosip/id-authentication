package io.mosip.authentication.core.authtype.dto;

import java.util.Map;

import lombok.Data;

/**
 * 
 * @author Manoj SP
 *
 */
@Data
public class AuthtypeStatus {

	private String authType;
	private String authSubType;
	private Boolean locked;
	private Long unlockForSeconds;
	private String requestId;
	private Map<String, Object> metadata;
}
