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
import org.mosip.registration.dao.RegistrationCenterDAO;
import org.mosip.registration.dao.RegistrationUserDetailDAO;
import org.mosip.registration.entity.RegistrationUserDetail;
import org.mosip.registration.repositories.RegistrationUserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * The implementation class of {@link RegistrationCenterDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationUserDetailDAOImpl implements RegistrationUserDetailDAO {
	
	/** The registrationUserDetail repository. */
	@Autowired
	private RegistrationUserDetailRepository registrationUserDetailRepository;
	
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
	 * @see org.mosip.registration.dao.RegistrationUserDetailDAO#getUserDetail(java.lang.String)
	 */
	public Map<String,String> getUserDetail(String userId){
		
		LOGGER.debug("REGISTRATION - USER_DETAIL - REGISTRATION_USER_DETAIL_DAO", 
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
				"Retrieving User details");
		
		List<RegistrationUserDetail> registrationUserDetail =registrationUserDetailRepository.findByIdAndIsActiveTrue(userId); 
		LinkedHashMap<String,String> userDetails = new LinkedHashMap<String,String>();
		if(!registrationUserDetail.isEmpty()) {
			userDetails.put("name",registrationUserDetail.get(0).getName());
			userDetails.put("centerId", registrationUserDetail.get(0).getCntrId());
		}
		
		LOGGER.debug("REGISTRATION - USER_DETAIL - REGISTRATION_USER_DETAIL_DAO", 
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
				"Retrieved User details");
		return userDetails;
	}

}
