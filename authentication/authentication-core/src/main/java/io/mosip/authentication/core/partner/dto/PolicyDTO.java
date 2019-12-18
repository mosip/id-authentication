package io.mosip.authentication.core.partner.dto;

import lombok.Data;

/**
 * PolicyDTO restricts the authorization type allowed,which is mapped with Auth Policies Json.
 * @author Arun Bose S
 */
@Data
public class PolicyDTO {
	
	private String policyId;
	
	/** The policies. */
	private Policies policies;
}
