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

	/**
	 * Instance of LOGGER
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/** The registrationUserDetail repository. */
	@Autowired
	private RegistrationUserDetailRepository registrationUserDetailRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.dao.RegistrationUserDetailDAO#getUserDetail(java.lang.
	 * String)
	 */
	public Map<String, String> getUserDetail(String userId) {

		LOGGER.debug("REGISTRATION - USER_DETAIL - REGISTRATION_USER_DETAIL_DAO_IMPL",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), "Fetching User details");

		List<RegistrationUserDetail> registrationUserDetail = registrationUserDetailRepository
				.findByIdAndIsActiveTrue(userId);
		LinkedHashMap<String, String> userDetails = new LinkedHashMap<>();

		if (!registrationUserDetail.isEmpty()) {
			userDetails.put("name", registrationUserDetail.get(0).getName());
			userDetails.put("centerId", registrationUserDetail.get(0).getCntrId());
		}

		LOGGER.debug("REGISTRATION - USER_DETAIL - REGISTRATION_USER_DETAIL_DAO_IMPL",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"User details fetched successfully");

		return userDetails;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.dao.RegistrationUserDetailDAO#getUserStatus(java.lang.
	 * String)
	 */
	public String getUserStatus(String userId) {

		LOGGER.debug("REGISTRATION - USER_STATUS - REGISTRATION_USER_DETAIL_DAO_IMPL",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), "Fetching User starus");

		List<RegistrationUserDetail> registrationUserDetail = registrationUserDetailRepository
				.findByIdAndIsActiveTrue(userId);
		String userCheck = "";
		if (!registrationUserDetail.isEmpty()) {
			userCheck = registrationUserDetail.get(0).getUserStatus();
		}

		LOGGER.debug("REGISTRATION - USER_STATUS - REGISTRATION_USER_DETAIL_DAO_IMPL",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"User starus fetched successfully");

		return userCheck;
	}

}
