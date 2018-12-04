package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.RegistrationScreenAuthorizationDAO;
import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.entity.RegistrationScreenAuthorization;
import io.mosip.registration.repositories.RegistrationScreenAuthorizationRepository;

/**
 * The implementation class of {@link RegistrationScreenAuthorizationDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationScreenAuthorizationDAOImpl implements RegistrationScreenAuthorizationDAO {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationScreenAuthorizationDAOImpl.class);

	/** The registrationScreenAuthorization repository. */
	@Autowired
	private RegistrationScreenAuthorizationRepository registrationScreenAuthorizationRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.dao.RegistrationScreenAuthorizationDAO#
	 * getScreenAuthorizationDetails(java.lang.String)
	 */
	public AuthorizationDTO getScreenAuthorizationDetails(String roleCode) {

		LOGGER.debug("REGISTRATION - SCREEN_AUTHORIZATION - REGISTRATION_SCREEN_AUTHORIZATION_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID,
				"Fetching List of Screens to be authorized");

		AuthorizationDTO authorizationDTO = new AuthorizationDTO();
		List<RegistrationScreenAuthorization> authorizationList = registrationScreenAuthorizationRepository
				.findByRegistrationScreenAuthorizationIdRoleCodeAndIsPermittedTrueAndIsActiveTrue(roleCode);

		List<String> authList = new ArrayList<>();
		authorizationList.forEach(auth -> authList.add(auth.getRegistrationScreenAuthorizationId().getScreenId()));
		authorizationDTO.setAuthorizationScreenId(authList);
		authorizationDTO.setAuthorizationRoleCode(roleCode);
		if (!authorizationList.isEmpty()) {
			authorizationDTO.setAuthorizationIsPermitted(authorizationList.get(0).isPermitted());

		}
		LOGGER.debug("REGISTRATION - SCREEN_AUTHORIZATION - REGISTRATION_SCREEN_AUTHORIZATION_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID,
				"List of Screens to be authorized are fetched successfully");

		return authorizationDTO;
	}
}
