package io.mosip.registration.dao.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegistrationAppLoginDAO;
import io.mosip.registration.entity.RegistrationAppLoginMethod;
import io.mosip.registration.repositories.RegistrationAppLoginRepository;

/**
 * The implementation class of {@link RegistrationAppLoginDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationAppLoginDAOImpl implements RegistrationAppLoginDAO {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationAppLoginDAOImpl.class);

	/** The registrationAppLogin repository. */
	@Autowired
	private RegistrationAppLoginRepository registrationAppLoginRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.dao.RegistrationAppLoginDAO#getModesOfLogin()
	 */
	public Map<String, Object> getModesOfLogin() {

		LOGGER.debug("REGISTRATION - LOGINMODES - REGISTRATION_APP_LOGIN_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Fetching list of login modes");

		List<RegistrationAppLoginMethod> loginList = registrationAppLoginRepository
				.findByIsActiveTrueOrderByMethodSeq();

		Map<String, Object> loginModes = new LinkedHashMap<>();
		loginList.forEach(mode -> loginModes.put(String.valueOf(mode.getMethodSeq()), mode.getRegistrationAppLoginMethodId().getLoginMethod()));
		
		LOGGER.debug("REGISTRATION - LOGINMODES - REGISTRATION_APP_LOGIN_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "List of login modes fetched successfully");
		
		loginModes.put(RegistrationConstants.LOGIN_SEQUENCE, RegistrationConstants.PARAM_ONE);
		return loginModes;
	}
}
