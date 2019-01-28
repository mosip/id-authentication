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
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.repositories.UserDetailRepository;
import io.mosip.registration.repositories.UserBiometricRepository;

/**
 * The implementation class of {@link RegistrationCenterDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
@Transactional
public class UserDetailDAOImpl implements UserDetailDAO {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(UserDetailDAOImpl.class);

	/** The userDetail repository. */
	@Autowired
	private UserDetailRepository userDetailRepository;
	
	@Autowired
	private UserBiometricRepository userBiometricRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.dao.RegistrationUserDetailDAO#getUserDetail(java.lang.
	 * String)
	 */
	public UserDetail getUserDetail(String userId) {

		LOGGER.debug("REGISTRATION - USER_DETAIL - REGISTRATION_USER_DETAIL_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID, "Fetching User details");

		List<UserDetail> userDetail = userDetailRepository
				.findByIdIgnoreCaseAndIsActiveTrue(userId);
		
		LOGGER.debug("REGISTRATION - USER_DETAIL - REGISTRATION_USER_DETAIL_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID,
				"User details fetched successfully");

		return !userDetail.isEmpty() ? userDetail.get(0) : null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.UserDetailDAO#updateLoginParams(io.
	 * mosip.registration.entity.UserDetail)
	 */
	public void updateLoginParams(UserDetail userDetail) {
		
		LOGGER.debug("REGISTRATION - UPDATE_LOGIN_PARAMS - REGISTRATION_USER_DETAIL_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID, "Updating Login params");

		userDetailRepository.save(userDetail);
		
		LOGGER.debug("REGISTRATION - UPDATE_LOGIN_PARAMS - REGISTRATION_USER_DETAIL_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID, "Updated Login params successfully");

	}
	
	public List<UserBiometric> getAllActiveUsers(String attrCode) {
		return userBiometricRepository.findByUserBiometricIdBioAttributeCodeAndIsActiveTrue(attrCode);
		
	}
	
	public List<UserBiometric> getUserSpecificBioDetails(String userId, String bioType){
		return userBiometricRepository.findByUserBiometricIdUsrIdAndIsActiveTrueAndUserBiometricIdBioTypeCodeIgnoreCase(userId, bioType);
	}
}
