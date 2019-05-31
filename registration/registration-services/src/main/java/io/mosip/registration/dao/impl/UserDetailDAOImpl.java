package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_DETAIL;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_DETAIL_DAO;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.UserDetailResponseDto;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.entity.UserPassword;
import io.mosip.registration.entity.UserRole;
import io.mosip.registration.entity.id.UserRoleID;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.UserBiometricRepository;
import io.mosip.registration.repositories.UserDetailRepository;
import io.mosip.registration.repositories.UserPwdRepository;
import io.mosip.registration.repositories.UserRoleRepository;

/**
 * The implementation class of {@link UserDetailDAO}.
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

	/** The userPwd repository. */
	@Autowired
	private UserPwdRepository userPwdRepository;

	/** The userRole repository. */
	@Autowired
	private UserRoleRepository userRoleRepository;

	/** The userBiometric repository. */
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

		LOGGER.info("REGISTRATION - USER_DETAIL - REGISTRATION_USER_DETAIL_DAO_IMPL", APPLICATION_NAME, APPLICATION_ID,
				"Fetching User details");

		List<UserDetail> userDetail = userDetailRepository.findByIdIgnoreCaseAndIsActiveTrue(userId);

		LOGGER.info("REGISTRATION - USER_DETAIL - REGISTRATION_USER_DETAIL_DAO_IMPL", APPLICATION_NAME, APPLICATION_ID,
				"User details fetched successfully");

		return !userDetail.isEmpty() ? userDetail.get(0) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.UserDetailDAO#updateLoginParams(io.
	 * mosip.registration.entity.UserDetail)
	 */
	public void updateLoginParams(UserDetail userDetail) {

		LOGGER.info("REGISTRATION - UPDATE_LOGIN_PARAMS - REGISTRATION_USER_DETAIL_DAO_IMPL", APPLICATION_NAME,
				APPLICATION_ID, "Updating Login params");

		userDetailRepository.save(userDetail);

		LOGGER.info("REGISTRATION - UPDATE_LOGIN_PARAMS - REGISTRATION_USER_DETAIL_DAO_IMPL", APPLICATION_NAME,
				APPLICATION_ID, "Updated Login params successfully");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.dao.RegistrationUserDetailDAO#getAllActiveUsers(java.
	 * lang. String)
	 */
	public List<UserBiometric> getAllActiveUsers(String attrCode) {

		LOGGER.info("REGISTRATION - ACTIVE_USERS - REGISTRATION_USER_DETAIL_DAO_IMPL", APPLICATION_NAME, APPLICATION_ID,
				"Fetching all active users");

		return userBiometricRepository.findByUserBiometricIdBioAttributeCodeAndIsActiveTrue(attrCode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.dao.RegistrationUserDetailDAO#
	 * getUserSpecificBioDetails(java.lang. String, java.lang.String)
	 */
	public List<UserBiometric> getUserSpecificBioDetails(String userId, String bioType) {

		LOGGER.info("REGISTRATION - USER_SPECIFIC_BIO - REGISTRATION_USER_DETAIL_DAO_IMPL", APPLICATION_NAME,
				APPLICATION_ID, "Fetching user specific biometric details");

		return userBiometricRepository
				.findByUserBiometricIdUsrIdAndIsActiveTrueAndUserBiometricIdBioTypeCodeIgnoreCase(userId, bioType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.UserDetailDAO#save(io.mosip.registration.dto.
	 * UserDetailResponseDto)
	 */
	public void save(UserDetailResponseDto userDetailsResponse) throws RegBaseUncheckedException {

		LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID, "Entering user detail save method...");

		List<UserDetail> userList = new ArrayList<>();
		List<UserPassword> userPassword = new ArrayList<>();
		try {

			userDetailsResponse.getUserDetails().forEach(userDtals -> {

				UserDetail userDtls = new UserDetail();
				UserPassword usrPwd = new UserPassword();
				// password details
				usrPwd.setUsrId(userDtals.getUserName());
				usrPwd.setPwd(CryptoUtil.encodeBase64(userDtals.getUserPassword()));
				usrPwd.setStatusCode("00");
				usrPwd.setIsActive(true);
				usrPwd.setLangCode(RegistrationConstants.ENGLISH_LANG_CODE);
				if (SessionContext.isSessionContextAvailable()) {
					usrPwd.setCrBy(SessionContext.userContext().getUserId());
				} else {
					usrPwd.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
				}
				usrPwd.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				userPassword.add(usrPwd);

				userDtls.setId(userDtals.getUserName());
				userDtls.setUserPassword(usrPwd);
				userDtls.setEmail(userDtals.getMail());
				userDtls.setMobile(userDtals.getMobile());
				userDtls.setName(userDtals.getName());
				userDtls.setLangCode(RegistrationConstants.ENGLISH_LANG_CODE);
				if (SessionContext.isSessionContextAvailable()) {
					userDtls.setCrBy(SessionContext.userContext().getUserId());
				} else {
					userDtls.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
				}
				userDtls.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				userDtls.setIsActive(true);
				userDtls.setStatusCode("00");
				userList.add(userDtls);

			});

			userDetailRepository.saveAll(userList);

			userPwdRepository.saveAll(userPassword);

			userDetailsResponse.getUserDetails().forEach(role -> {

				UserRole roles = new UserRole();
				roles.setIsActive(true);
				roles.setLangCode(RegistrationConstants.ENGLISH_LANG_CODE);
				if (SessionContext.isSessionContextAvailable()) {
					roles.setCrBy(SessionContext.userContext().getUserId());
				} else {
					roles.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
				}
				roles.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				role.getRoles().forEach(rol -> {
					UserRoleID roleId = new UserRoleID();
					roleId.setRoleCode(rol);
					roleId.setUsrId(role.getUserName());
					roles.setUserRoleID(roleId);
					userRoleRepository.save(roles);
				});

			});
			
			

			LOGGER.info(LOG_REG_USER_DETAIL, APPLICATION_NAME, APPLICATION_ID, "Leaving user detail save method...");

		} catch (RuntimeException exRuntimeException) {
			LOGGER.error(LOG_REG_USER_DETAIL_DAO, APPLICATION_NAME, APPLICATION_ID,
					exRuntimeException.getMessage() + ExceptionUtils.getStackTrace(exRuntimeException));
			throw new RegBaseUncheckedException(LOG_REG_USER_DETAIL_DAO, exRuntimeException.getMessage());
		}
	}

	@Override
	public UserBiometric getUserSpecificBioDetail(String userId, String bioType, String subType) {
		LOGGER.info("REGISTRATION - USER_SPECIFIC_BIO - REGISTRATION_USER_DETAIL_DAO_IMPL", APPLICATION_NAME,
				APPLICATION_ID, "Fetching user specific subtype level biometric detail");

		return userBiometricRepository
				.findByUserBiometricIdUsrIdAndIsActiveTrueAndUserBiometricIdBioTypeCodeAndUserBiometricIdBioAttributeCodeIgnoreCase(userId, bioType, subType);
	}

}
