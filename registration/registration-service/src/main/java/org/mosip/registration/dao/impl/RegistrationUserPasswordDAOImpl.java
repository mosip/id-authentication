package org.mosip.registration.dao.impl;

import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.util.List;

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.dao.RegistrationUserPasswordDAO;
import org.mosip.registration.entity.RegistrationUserPassword;
import org.mosip.registration.entity.RegistrationUserPasswordID;
import org.mosip.registration.repositories.RegistrationUserPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * The implementation class of {@link RegistrationUserPasswordDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationUserPasswordDAOImpl  implements RegistrationUserPasswordDAO {
	
	/** The registrationUserPassword repository. */
	@Autowired
	private RegistrationUserPasswordRepository registrationUserPasswordRepository;
	
	/** Object for Logger. */
	private static MosipLogger LOGGER;

	/**
	 * Initialize logger.
	 *
	 * @param mosipRollingFileAppender the mosip rolling file appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}
	
	/* (non-Javadoc)
	 * @see org.mosip.registration.dao.RegistrationUserPasswordDAO#getPassword(java.lang.String,java.lang.String)
	 */
	public String getPassword(String userId, String hashPassword) {
		
		LOGGER.debug("REGISTRATION - USER_PASSWORD - REGISTRATION_USER_PASSWORD_DAO", 
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
				"Retrieving User Password");
		
		RegistrationUserPasswordID registrationUserPasswordID = new RegistrationUserPasswordID();
		registrationUserPasswordID.setUsrId(userId);
		registrationUserPasswordID.setPwd(hashPassword);
		List<RegistrationUserPassword> registrationUserPassword = registrationUserPasswordRepository.findByRegistrationUserPasswordID(registrationUserPasswordID);
		String userData = "";
		if(!registrationUserPassword.isEmpty()) {
			userData = registrationUserPassword.get(0).getRegistrationUserPasswordID().getPwd();
		}
		
		LOGGER.debug("REGISTRATION - USER_PASSWORD - REGISTRATION_USER_PASSWORD_DAO", 
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
				"Retrieved User Password");
		
		return userData;
	}

}
