package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
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

		LOGGER.debug("REGISTRATION - LOGINMODES - REGISTRATION_APP_LOGIN_DAO_IMPL", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Fetching list of login modes");

		List<RegistrationAppLoginMethod> loginList = registrationAppLoginRepository
				.findByIsActiveTrueOrderByMethodSeq();

		Map<String, Object> loginModes = new LinkedHashMap<>();
		for (int mode = 0; mode < loginList.size(); mode++) {
			loginModes.put("" + loginList.get(mode).getMethodSeq(),
					loginList.get(mode).getRegistrationAppLoginMethodId().getLoginMethod());
		}

		LOGGER.debug("REGISTRATION - LOGINMODES - REGISTRATION_APP_LOGIN_DAO_IMPL", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "List of login modes fetched successfully");

		loginModes.put("sequence", 1);
		return loginModes;
	}
}
