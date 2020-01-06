package io.mosip.registration.util.advice;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.SecurityContext;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.dto.UserRoleDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.login.LoginService;

/**
 * Helps to authenticate the current user details before proceeding the method
 * call
 * 
 * @author Omsai Eswar Mulakaluri
 *
 */
@Aspect
@Component
public class AuthenticationAdvice {

	public static final String OFFICER_ROLE = "REGISTRATION_OFFICER";
	public static final String SUPERVISOR_ROLE = "REGISTRATION_SUPERVISOR";
	public static final String ADMIN_ROLE = "REGISTRATION_ADMIN";

	@Autowired
	private LoginService loginService;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = AppConfig.getLogger(AuthenticationAdvice.class);

	/**
	 * Validate the current user id details against the DB before proceeding the key
	 * methods On successful validation only the method return to method handling
	 * 
	 * @param preAuthorizeUserId
	 *            - the {@link PreAuthorizeUserId}
	 * @throws RegBaseCheckedException
	 *             - generalised exception with errorCode and errorMessage
	 */
	@Before(value = "@annotation(preAuthorizeUserId)")
	public void authorizeUserId(PreAuthorizeUserId preAuthorizeUserId) throws RegBaseCheckedException {
		boolean roleFound = false;

		LOGGER.info(LoggerConstants.AUTHORIZE_USER_ID, APPLICATION_ID, APPLICATION_NAME,
				"Pre-Authorize the user id starting with roles " + preAuthorizeUserId.roles());

		if (SessionContext.isSessionContextAvailable()) {
			SecurityContext securityContext = SessionContext.securityContext();
			
			if(null == securityContext || RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM == securityContext.getUserId()) {
				LOGGER.info(LoggerConstants.AUTHORIZE_USER_ID, APPLICATION_ID, APPLICATION_NAME,
						"Pre-Authorize the user id got failed");

				throw new RegBaseCheckedException(RegistrationExceptionConstants.AUTH_ADVICE_USR_ERROR.getErrorCode(), RegistrationExceptionConstants.AUTH_ADVICE_USR_ERROR.getErrorMessage());

			}

			UserDTO userDTO = loginService.getUserDetail(securityContext.getUserId());
			
			if(null == userDTO) {
				LOGGER.info(LoggerConstants.AUTHORIZE_USER_ID, APPLICATION_ID, APPLICATION_NAME,
						"Pre-Authorize the user id got failed");

				throw new RegBaseCheckedException(RegistrationExceptionConstants.AUTH_ADVICE_USR_ERROR.getErrorCode(), RegistrationExceptionConstants.AUTH_ADVICE_USR_ERROR.getErrorMessage());
			}

			List<String> roleList = userDTO.getUserRole().stream().map(UserRoleDTO::getRoleCode)
					.collect(Collectors.toList());

			for (String role : preAuthorizeUserId.roles()) {
				roleFound = roleList.stream().anyMatch(dbrole -> dbrole.equalsIgnoreCase(role));
				if (roleFound)
					break;
			}

			if (!(userDTO.getIsActive() && roleFound)) {
				LOGGER.info(LoggerConstants.AUTHORIZE_USER_ID, APPLICATION_ID, APPLICATION_NAME,
						"Pre-Authorize the user id got failed");

				throw new RegBaseCheckedException(RegistrationExceptionConstants.AUTH_ADVICE_USR_ERROR.getErrorCode(), RegistrationExceptionConstants.AUTH_ADVICE_USR_ERROR.getErrorMessage());
			}

			LOGGER.info(LoggerConstants.AUTHORIZE_USER_ID, APPLICATION_ID, APPLICATION_NAME,
					"Pre-Authorize the user id successfully completed");
		} else {
			LOGGER.info(LoggerConstants.AUTHORIZE_USER_ID, APPLICATION_ID, APPLICATION_NAME,
					"Pre-Authorize the user id got failed");

			throw new RegBaseCheckedException(RegistrationExceptionConstants.AUTH_ADVICE_USR_ERROR.getErrorCode(), RegistrationExceptionConstants.AUTH_ADVICE_USR_ERROR.getErrorMessage());
		}
	}
}
