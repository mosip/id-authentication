package io.mosip.registration.util.advice;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.SecurityContext;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
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

	@Autowired
	private LoginService loginService;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = AppConfig.getLogger(AuthenticationAdvice.class);

	/**
	 * Validate the current user id details against the DB before proceeding the key methods
	 * On successful validation only the method return to method handling
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@Before("@annotation(PreAuthorizeUserId)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		LOGGER.info(LoggerConstants.AUTHORIZE_USER_ID, APPLICATION_ID, APPLICATION_NAME,
				"Pre-Authorize the user id starting");

		if (SessionContext.isSessionContextAvailable()) {
			SecurityContext securityContext = SessionContext.securityContext();

			UserDetail userDetail = loginService.getUserDetail(securityContext.getUserId());
			List<String> roleList = new ArrayList<>();

			userDetail.getUserRole().forEach(roleCode -> {
				if (roleCode.getIsActive()) {
					roleList.add(String.valueOf(roleCode.getUserRoleID().getRoleCode()));
				}
			});

			LOGGER.info(LoggerConstants.AUTHORIZE_USER_ID, APPLICATION_ID, APPLICATION_NAME,
					"Pre-Authorize the user id successfully completed");

			if(userDetail.getIsActive() && roleList.containsAll(securityContext.getRoles())) return joinPoint.proceed();
		}

		LOGGER.info(LoggerConstants.AUTHORIZE_USER_ID, APPLICATION_ID, APPLICATION_NAME,
				"Pre-Authorize the user id got failed");

		throw new RegBaseCheckedException("REG-SER-ATAD", "Invalid user id.");
	}
}
