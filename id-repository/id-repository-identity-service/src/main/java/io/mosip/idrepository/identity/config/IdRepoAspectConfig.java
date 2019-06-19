package io.mosip.idrepository.identity.config;

import java.time.Duration;
import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;

import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * @author Manoj SP
 *
 */
//@Aspect
//@Configuration
public class IdRepoAspectConfig {

	private Logger mosipLogger = IdRepoLogger.getLogger(IdRepoAspectConfig.class);

	private LocalDateTime startTime;
	private LocalDateTime jsonSchemaValidatorStartTime;

	@Before("execution(* io.mosip.kernel.idrepo.*.*.*(..))"
			+ "&& !execution(* io.mosip.kernel.idrepo.httpfilter.*.*(..))")
	public void before(JoinPoint joinPoint) {
		startTime = DateUtils.getUTCCurrentDateTime();
	}

	@After("execution(* io.mosip.kernel.idrepo.*.*.*(..))"
			+ "&& !execution(* io.mosip.kernel.idrepo.httpfilter.*.*(..))")
	public void after(JoinPoint joinPoint) {
		mosipLogger.debug(IdRepoLogger.getUin(), "IdRepoAspectConfig", joinPoint.toString(),
				"Time taken for execution - "
						+ Duration.between(startTime, DateUtils.getUTCCurrentDateTime()).toMillis());
	}

	@Before("execution(* io.mosip.kernel.idobjectvalidator.impl.*.*(..))")
	public void beforeJsonSchemaValidator(JoinPoint joinPoint) {
		jsonSchemaValidatorStartTime = DateUtils.getUTCCurrentDateTime();
	}

	@After("execution(* io.mosip.kernel.idobjectvalidator.impl.*.*(..))")
	public void afterJsonSchemaValidator(JoinPoint joinPoint) {
		mosipLogger.debug(IdRepoLogger.getUin(), "IdRepoAspectConfig", joinPoint.toString(),
				"Time taken for execution - "
						+ Duration.between(jsonSchemaValidatorStartTime, DateUtils.getUTCCurrentDateTime()).toMillis());
	}
}