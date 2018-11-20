package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
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
@Transactional
public class RegistrationUserDetailDAOImpl implements RegistrationUserDetailDAO {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationUserDetailDAOImpl.class);

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
	public RegistrationUserDetail getUserDetail(String userId) {

		LOGGER.debug("REGISTRATION - USER_DETAIL - REGISTRATION_USER_DETAIL_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID, "Fetching User details");

		List<RegistrationUserDetail> registrationUserDetail = registrationUserDetailRepository
				.findByIdAndIsActiveTrue(userId);
		
		LOGGER.debug("REGISTRATION - USER_DETAIL - REGISTRATION_USER_DETAIL_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID,
				"User details fetched successfully");

		return !registrationUserDetail.isEmpty() ? registrationUserDetail.get(0) : null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.RegistrationUserDetailDAO#updateLoginParams(io.
	 * mosip.registration.entity.RegistrationUserDetail)
	 */
	public void updateLoginParams(RegistrationUserDetail registrationUserDetail) {
		
		LOGGER.debug("REGISTRATION - UPDATE_LOGIN_PARAMS - REGISTRATION_USER_DETAIL_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID, "Updating Login params");

		registrationUserDetailRepository.save(registrationUserDetail);
		
		LOGGER.debug("REGISTRATION - UPDATE_LOGIN_PARAMS - REGISTRATION_USER_DETAIL_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID, "Updated Login params successfully");

	}
}
