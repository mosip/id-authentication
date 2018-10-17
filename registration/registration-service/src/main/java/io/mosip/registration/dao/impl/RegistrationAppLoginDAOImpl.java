package io.mosip.registration.dao.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.RegConstants;
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
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/** The registrationAppLogin repository. */
	@Autowired
	private RegistrationAppLoginRepository registrationAppLoginRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.dao.RegistrationAppLoginDAO#getModesOfLogin()
	 */
	public Map<String, Object> getModesOfLogin() {

		LOGGER.debug("REGISTRATION - LOGINMODES - REGISTRATION_APP_LOGIN_DAO_IMPL", RegConstants.APPLICATION_NAME,
				RegConstants.APPLICATION_ID, "Fetching list of login modes");

		List<RegistrationAppLoginMethod> loginList = registrationAppLoginRepository
				.findByIsActiveTrueOrderByMethodSeq();

		Map<String, Object> loginModes = new LinkedHashMap<>();
		loginList.forEach(mode -> loginModes.put(String.valueOf(mode.getMethodSeq()), mode.getRegistrationAppLoginMethodId().getLoginMethod()));
		
		LOGGER.debug("REGISTRATION - LOGINMODES - REGISTRATION_APP_LOGIN_DAO_IMPL", RegConstants.APPLICATION_NAME,
				RegConstants.APPLICATION_ID, "List of login modes fetched successfully");
		
		loginModes.put(RegConstants.LOGIN_SEQUENCE, RegConstants.INITIAL_LOGIN_SEQUENCE);
		return loginModes;
	}
}
