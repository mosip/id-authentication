package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
	 * @see io.mosip.registration.dao.RegistrationAppLoginDAO#getModesOfLogin()
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
