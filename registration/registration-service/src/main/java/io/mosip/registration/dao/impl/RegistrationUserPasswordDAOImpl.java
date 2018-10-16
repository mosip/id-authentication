package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.dao.RegistrationUserPasswordDAO;
import io.mosip.registration.entity.RegistrationUserPassword;
import io.mosip.registration.repositories.RegistrationUserPasswordRepository;

/**
 * The implementation class of {@link RegistrationUserPasswordDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationUserPasswordDAOImpl implements RegistrationUserPasswordDAO {

	/**
	 * Instance of LOGGER
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/** The registrationUserPassword repository. */
	@Autowired
	private RegistrationUserPasswordRepository registrationUserPasswordRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.dao.RegistrationUserPasswordDAO#getPassword(java.lang.
	 * String,java.lang.String)
	 */
	public boolean getPassword(String userId, String hashPassword) {

		LOGGER.debug("REGISTRATION - USER_CREDENTIALS - REGISTRATION_USER_PASSWORD_DAO_IMPL",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), "Fetching User credentials");

		List<RegistrationUserPassword> registrationUserPwd = registrationUserPasswordRepository
				.findByRegistrationUserPasswordIdUsrIdAndIsActiveTrue(userId);

		String userData = !registrationUserPwd.isEmpty() ? registrationUserPwd.get(0).getPwd() : null;

		LOGGER.debug("REGISTRATION - USER_CREDENTIALS - REGISTRATION_USER_PASSWORD_DAO_IMPL",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"User credentials fetched successfully");

		return userData != null && hashPassword.equals(userData);
	}

}
