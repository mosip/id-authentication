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

import io.mosip.registration.dao.RegistrationCenterDAO;
import io.mosip.registration.dao.RegistrationUserDetailDAO;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.repositories.RegistrationUserDetailRepository;

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
	 * @see io.mosip.registration.dao.RegistrationUserDetailDAO#getUserDetail(java.lang.String)
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
