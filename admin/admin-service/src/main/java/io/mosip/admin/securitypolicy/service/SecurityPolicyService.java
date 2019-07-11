package io.mosip.admin.securitypolicy.service;
/**
 * Security Policy Service interface
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */

import io.mosip.admin.securitypolicy.dto.AuthFactorsDto;

public interface SecurityPolicyService {

	/**
	 * method to get the available authentication factors available for the user
	 * 
	 * @param username
	 *            name of the user
	 * @return {@link AuthFactorsDto}
	 */
	public AuthFactorsDto getSecurityPolicy(String username);

}
