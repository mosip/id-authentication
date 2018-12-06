package io.mosip.registration.dao.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegistrationAppAuthenticationDAO;
import io.mosip.registration.entity.RegistrationAppAuthenticationMethod;
import io.mosip.registration.repositories.RegistrationAppAuthenticationRepository;

/**
 * The implementation class of {@link RegistrationAppAuthenticationDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationAppAuthenticationDAOImpl implements RegistrationAppAuthenticationDAO {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationAppAuthenticationDAOImpl.class);

	/** The registrationAppLogin repository. */
	@Autowired
	private RegistrationAppAuthenticationRepository registrationAppLoginRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.dao.RegistrationAppLoginDAO#getModesOfLogin()
	 */
	public Map<String, Object> getModesOfLogin(String authType) {

		LOGGER.debug("REGISTRATION - LOGINMODES - REGISTRATION_APP_LOGIN_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Fetching list of login modes");

		List<RegistrationAppAuthenticationMethod> loginList = registrationAppLoginRepository
				.findByIsActiveTrueAndRegistrationAppAuthenticationMethodIdProcessNameOrderByMethodSeq(authType);

		Map<String, Object> loginModes = loginList.stream().collect(
                Collectors.toMap(registrationAppAuthenticationMethod -> String.valueOf(registrationAppAuthenticationMethod.getMethodSeq()), 
                		registrationAppAuthenticationMethod -> registrationAppAuthenticationMethod.getregistrationAppAuthenticationMethodId().getLoginMethod()));
		
		LOGGER.debug("REGISTRATION - LOGINMODES - REGISTRATION_APP_LOGIN_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "List of login modes fetched successfully");
		
		loginModes.put(RegistrationConstants.LOGIN_SEQUENCE, RegistrationConstants.PARAM_ONE);
		return loginModes;
	}
}
