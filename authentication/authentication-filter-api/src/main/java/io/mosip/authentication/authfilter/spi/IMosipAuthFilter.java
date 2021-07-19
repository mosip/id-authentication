package io.mosip.authentication.authfilter.spi;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;

/**
 * The Interface IMosipAuthFilter - to implement a filter predicate on the
 * authentication request and identity data to proceed further or on based on
 * the test condition.
 * 
 * @author Loganathan Sekar
 */
public interface IMosipAuthFilter {

	/**
	 * Inits the.
	 */
	void init() throws IdAuthenticationFilterException;

	/**
	 * Test method that executes predicate test condition on the given arguments
	 *
	 * @param authRequest  the auth request
	 * @param identityData the identity data
	 * @param properties   the properties
	 * @throws IdAuthenticationFilterException the IdAuthenticationFilterException in case of any validation
	 *                                         failure with appropriate error code and error message
	 */
	void validate(AuthRequestDTO authRequest, Map<String, List<IdentityInfoDTO>> identityData,
			Map<String, Object> properties) throws IdAuthenticationFilterException;
}
