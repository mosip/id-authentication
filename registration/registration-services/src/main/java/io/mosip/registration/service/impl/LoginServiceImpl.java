package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.dao.AppAuthenticationDAO;
import io.mosip.registration.dao.RegistrationCenterDAO;
import io.mosip.registration.dao.ScreenAuthorizationDAO;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.service.LoginService;

/**
 * Class for implementing login service
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Service
public class LoginServiceImpl implements LoginService {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(LoginServiceImpl.class);

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	/**
	 * Class to retrieve the Login Details from DB
	 */
	@Autowired
	private AppAuthenticationDAO appAuthenticationDAO;

	/**
	 * Class to retrieve the Officer Details from DB
	 */
	@Autowired
	private UserDetailDAO userDetailDAO;

	/**
	 * Class to retrieve the Registration Center details from DB
	 */
	@Autowired
	private RegistrationCenterDAO registrationCenterDAO;

	/**
	 * Class to retrieve the Screen authorization from DB
	 */
	@Autowired
	private ScreenAuthorizationDAO screenAuthorizationDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.login.LoginService#getModesOfLogin()
	 */
	@Override
	public List<String> getModesOfLogin(String authType, Set<String> roleList) {
		// Retrieve Login information

		LOGGER.info("REGISTRATION - LOGINMODES - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Fetching list of login modes");

		auditFactory.audit(AuditEvent.LOGIN_MODES_FETCH, Components.LOGIN_MODES, "Fetching list of login modes",
				"refId", "refIdType");

		return appAuthenticationDAO.getModesOfLogin(authType, roleList);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.service.login.LoginService#getUserDetail(java.lang.
	 * String)
	 */
	@Override
	public UserDetail getUserDetail(String userId) {
		// Retrieving Officer details
		LOGGER.info("REGISTRATION - USERDETAIL - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Fetching User details");

		auditFactory.audit(AuditEvent.FETCH_USR_DET, Components.USER_DETAIL, "Fetching User details", "refId",
				"refIdType");

		return userDetailDAO.getUserDetail(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.login.LoginService#
	 * getRegistrationCenterDetails(java.lang.String)
	 */
	@Override
	public RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId) {
		// Retrieving Registration Center details

		LOGGER.info("REGISTRATION - CENTERDETAILS - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Fetching Center details");

		auditFactory.audit(AuditEvent.FETCH_CNTR_DET, Components.CENTER_DETAIL, "Fetching Center details",
				"refId", "refIdType");

		return registrationCenterDAO.getRegistrationCenterDetails(centerId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.login.LoginService#
	 * getScreenAuthorizationDetails(java.lang.String)
	 */
	@Override
	public AuthorizationDTO getScreenAuthorizationDetails(List<String> roleCode) {
		// Fetching screen authorization details

		LOGGER.info("REGISTRATION - SCREENAUTHORIZATION - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Fetching list of Screens to be Authorized");

		auditFactory.audit(AuditEvent.FETCH_SCR_AUTH, Components.SCREEN_AUTH,
				"Fetching list of Screens to be Authorized", "refId", "refIdType");

		return screenAuthorizationDAO.getScreenAuthorizationDetails(roleCode);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.LoginService#updateLoginParams(io.mosip.
	 * registration.entity.UserDetail)
	 */
	public void updateLoginParams(UserDetail userDetail) {

		LOGGER.info("REGISTRATION - UPDATELOGINPARAMS - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Updating Login Params");

		userDetailDAO.updateLoginParams(userDetail);
		
		LOGGER.info("REGISTRATION - UPDATELOGINPARAMS - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Updated Login Params");

	}
}
