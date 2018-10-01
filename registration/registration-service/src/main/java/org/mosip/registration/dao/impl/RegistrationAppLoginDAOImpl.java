package org.mosip.registration.dao.impl;

import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.dao.RegistrationAppLoginDAO;
import org.mosip.registration.entity.RegistrationAppLoginMethod;
import org.mosip.registration.repositories.RegistrationAppLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * The implementation class of {@link RegistrationAppLoginDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationAppLoginDAOImpl implements RegistrationAppLoginDAO{

	/** The registrationAppLogin repository. */
	@Autowired
	private RegistrationAppLoginRepository registrationAppLoginRepository;
	
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
	 * @see org.mosip.registration.dao.RegistrationAppLoginDAO#getModesOfLogin()
	 */
	public Map<String,Object> getModesOfLogin(){
		
		LOGGER.debug("REGISTRATION - LOGIN_MODE - REGISTRATION_APP_LOGIN_DAO", 
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
				"Retrieve Login mode");
		
		List<RegistrationAppLoginMethod> loginList = registrationAppLoginRepository.findByIsActiveTrueOrderByMethodSeq();
		Map<String,Object> loginModes = new LinkedHashMap<String,Object>();
		for(int mode = 0; mode < loginList.size(); mode++) {
			loginModes.put(""+loginList.get(mode).getMethodSeq(), loginList.get(mode).getPk_applm_usr_id().getLoginMethod());
		}
		LOGGER.debug("REGISTRATION - LOGIN_MODE - REGISTRATION_APP_LOGIN_DAO", 
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
				"Retrieved correspondingLogin mode");
		return loginModes;
	}
}
