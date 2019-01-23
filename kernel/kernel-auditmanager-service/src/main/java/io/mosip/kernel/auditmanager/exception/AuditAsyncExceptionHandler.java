package io.mosip.kernel.auditmanager.exception;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auditmanager.logger.AuditManagerLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * Custom exception handler for AsyncTask
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class AuditAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final Logger LOGGER = AuditManagerLogger.getLogger(AuditAsyncExceptionHandler.class);

	@Override
	public void handleUncaughtException(final Throwable throwable, final Method method, final Object... obj) {
		for (final Object param : obj) {
			try {
				LOGGER.error("", "", "", MAPPER.writeValueAsString(param));
			} catch (JsonProcessingException e) {
				LOGGER.error("", "", "", e.getMessage());
			}
		}
	}

}