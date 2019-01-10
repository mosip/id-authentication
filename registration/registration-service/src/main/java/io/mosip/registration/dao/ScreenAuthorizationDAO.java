package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.dto.AuthorizationDTO;

/**
 * DAO class for RegistrationScreenAuthorization
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface ScreenAuthorizationDAO {

	/**
	 * This method is used to get the screen authorization
	 * 
	 * @return AuthorizationDTO of authorization details
	 */
	AuthorizationDTO getScreenAuthorizationDetails(List<String> roleCode);
}
