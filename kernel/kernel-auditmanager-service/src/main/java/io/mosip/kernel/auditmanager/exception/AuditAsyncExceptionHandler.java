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

	/**
	 * Field for ObjectMapper
	 */
	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * Field for Logger
	 */
	private static final Logger ERROR_LOGGER = AuditManagerLogger.getConsoleLogger(AuditAsyncExceptionHandler.class);

	/**
	 * Field for AUDIT_LOGGER
	 */
	private static final Logger AUDIT_LOGGER = AuditManagerLogger.getFileLogger(AuditAsyncExceptionHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler#
	 * handleUncaughtException(java.lang.Throwable, java.lang.reflect.Method,
	 * java.lang.Object[])
	 */
	@Override
	public void handleUncaughtException(final Throwable throwable, final Method method, final Object... obj) {
		ERROR_LOGGER.error("", "", "", "Exception message - " + throwable.getMessage());
		ERROR_LOGGER.error("", "", "", "Method name - " + method.getName());
		for (final Object param : obj) {
			try {
				AUDIT_LOGGER.error("", "", "", MAPPER.writeValueAsString(param));
			} catch (JsonProcessingException e) {
				ERROR_LOGGER.error("", "", "", e.getMessage());
			}
		}
	}

}