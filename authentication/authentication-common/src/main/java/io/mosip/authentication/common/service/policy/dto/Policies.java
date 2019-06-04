package io.mosip.authentication.common.service.policy.dto;

import lombok.Data;

/**
 * Policies restricts the authorization type allowed,which is mapped with Auth Policy Json.
 * @author Arun Bose S
 */
@Data
public class Policies {
	
	/** The policies. */
	private Policy policies;
}
