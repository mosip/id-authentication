package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.dao.RegistrationUserRoleDAO;
import io.mosip.registration.entity.RegistrationUserRole;
import io.mosip.registration.repositories.RegistrationUserRoleRepository;

/**
 * The implementation class of {@link RegistrationUserRoleDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationUserRoleDAOImpl implements RegistrationUserRoleDAO {

	/**
	 * Instance of LOGGER
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/** The registrationUserRole repository. */
	@Autowired
	private RegistrationUserRoleRepository registrationUserRoleRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.dao.RegistrationUserRoleDAO#getRoles(java.lang.String)
	 */
	public List<String> getRoles(String userId) {

		LOGGER.debug("REGISTRATION - USER_ROLES - REGISTRATION_USER_ROLE_DAO_IMPL", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Fetching User roles");

		List<RegistrationUserRole> registrationUserRoles = registrationUserRoleRepository
				.findByRegistrationUserRoleIdUsrIdAndIsActiveTrue(userId);

		List<String> userRole = new ArrayList<>();
		for(int role = 0; role < registrationUserRoles.size(); role++) {
			userRole.add(registrationUserRoles.get(role).getRegistrationUserRoleId().getRoleCode());
		}

		LOGGER.debug("REGISTRATION - USER_ROLES - REGISTRATION_USER_ROLE_DAO_IMPL", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "User roles fetched successfully");

		return userRole;
	}
}
