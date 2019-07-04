package io.mosip.idrepository.identity.config;

import java.time.Duration;
import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;

import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class IdRepoAspectConfig.
 *
 * @author Manoj SP
 */
//@Aspect
//@Configuration
public class IdRepoAspectConfig {

	/** The mosip logger. */
	private Logger mosipLogger = IdRepoLogger.getLogger(IdRepoAspectConfig.class);

	/** The start time. */
	private LocalDateTime startTime;
	
	/** The json schema validator start time. */
	private LocalDateTime jsonSchemaValidatorStartTime;

	/**
	 * Before.
	 *
	 * @param joinPoint the join point
	 */
	@Before("execution(* io.mosip.kernel.idrepo.*.*.*(..))"
			+ "&& !execution(* io.mosip.kernel.idrepo.httpfilter.*.*(..))")
	public void before(JoinPoint joinPoint) {
		startTime = DateUtils.getUTCCurrentDateTime();
	}

	/**
	 * After.
	 *
	 * @param joinPoint the join point
	 */
	@After("execution(* io.mosip.kernel.idrepo.*.*.*(..))"
			+ "&& !execution(* io.mosip.kernel.idrepo.httpfilter.*.*(..))")
	public void after(JoinPoint joinPoint) {
		mosipLogger.debug(IdRepoSecurityManager.getUser(), "IdRepoAspectConfig", joinPoint.toString(),
				"Time taken for execution - "
						+ Duration.between(startTime, DateUtils.getUTCCurrentDateTime()).toMillis());
	}

	/**
	 * Before json schema validator.
	 *
	 * @param joinPoint the join point
	 */
	@Before("execution(* io.mosip.kernel.idobjectvalidator.impl.*.*(..))")
	public void beforeJsonSchemaValidator(JoinPoint joinPoint) {
		jsonSchemaValidatorStartTime = DateUtils.getUTCCurrentDateTime();
	}

	/**
	 * After json schema validator.
	 *
	 * @param joinPoint the join point
	 */
	@After("execution(* io.mosip.kernel.idobjectvalidator.impl.*.*(..))")
	public void afterJsonSchemaValidator(JoinPoint joinPoint) {
		mosipLogger.debug(IdRepoSecurityManager.getUser(), "IdRepoAspectConfig", joinPoint.toString(),
				"Time taken for execution - "
						+ Duration.between(jsonSchemaValidatorStartTime, DateUtils.getUTCCurrentDateTime()).toMillis());
	}
}