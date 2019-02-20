package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationCenterDAO;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.UserDetailResponseDto;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.entity.UserPassword;
import io.mosip.registration.entity.UserRole;
import io.mosip.registration.entity.UserRoleID;
import io.mosip.registration.repositories.UserBiometricRepository;
import io.mosip.registration.repositories.UserDetailRepository;
import io.mosip.registration.repositories.UserPwdRepository;
import io.mosip.registration.repositories.UserRoleRepository;

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

	/** The userDetail repository. */
	@Autowired
	private UserPwdRepository userPwdRepository;

	/** The userDetail repository. */
	@Autowired
	private UserRoleRepository userRoleRepository;

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

	public List<UserBiometric> getAllActiveUsers(String attrCode) {
		return userBiometricRepository.findByUserBiometricIdBioAttributeCodeAndIsActiveTrue(attrCode);

	}

	public List<UserBiometric> getUserSpecificBioDetails(String userId, String bioType) {
		return userBiometricRepository
				.findByUserBiometricIdUsrIdAndIsActiveTrueAndUserBiometricIdBioTypeCodeIgnoreCase(userId, bioType);
	}

	public void save(UserDetailResponseDto userDetailsResponse) {

		List<UserDetail> userList = new ArrayList<>();
		List<UserPassword> userPassword = new ArrayList<>();
		userDetailsResponse.getUserDetails().forEach(userDtals -> {

			UserDetail userDtls = new UserDetail();
			UserPassword usrPwd = new UserPassword();
			// password details
			usrPwd.setUsrId(userDtals.getUserName());
			usrPwd.setPwd(userDtals.getUserPassword().toString());
			usrPwd.setStatusCode("00");
			usrPwd.setIsActive(true);
			usrPwd.setLangCode("eng");
			usrPwd.setCrBy(SessionContext.userContext().getUserId());
			usrPwd.setCrDtime(Timestamp.valueOf(LocalDateTime.now()));
			userPassword.add(usrPwd);

			userDtls.setId(userDtals.getUserName());
			userDtls.setUserPassword(usrPwd);
			userDtls.setEmail(userDtals.getMail());
			userDtls.setMobile(userDtals.getMobile());
			userDtls.setName(userDtals.getName());
			userDtls.setLangCode("eng");
			userDtls.setCrBy(SessionContext.userContext().getUserId());
			userDtls.setCrDtime(Timestamp.valueOf(LocalDateTime.now()));
			userDtls.setIsActive(true);
			userDtls.setStatusCode("00");
			userList.add(userDtls);

		});

		List<UserRole> userRole = new ArrayList<>();
		userDetailsResponse.getUserDetails().forEach(role -> {

			UserRole roles = new UserRole();
			roles.setIsActive(true);
			roles.setLangCode("eng");
			roles.setCrBy(SessionContext.userContext().getUserId());
			roles.setCrDtime(Timestamp.valueOf(LocalDateTime.now()));
			String uName = role.getUserName();
			role.getRoles().forEach(rol -> {
				UserRoleID roleId = new UserRoleID();
				roleId.setRoleCode(rol);
				roleId.setUsrId(uName);
				roles.setUserRoleID(roleId);
				userRole.add(roles);
			});

		});

		userDetailRepository.saveAll(userList);
		userPwdRepository.saveAll(userPassword);
		userRoleRepository.saveAll(userRole);
	}

}
