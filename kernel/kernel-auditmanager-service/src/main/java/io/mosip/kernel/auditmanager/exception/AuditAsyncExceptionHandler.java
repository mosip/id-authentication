package io.mosip.kernel.auditmanager.exception;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auditmanager.config.AuditManagerLogger;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler#
	 * handleUncaughtException(java.lang.Throwable, java.lang.reflect.Method,
	 * java.lang.Object[])
	 */
	@Override
	public void handleUncaughtException(final Throwable throwable, final Method method, final Object... obj) {
		AuditManagerLogger.consoleLoggerError("", "", "", "Exception message - " + throwable.getMessage());
		for (final Object param : obj) {
			try {
				AuditManagerLogger.fileLoggerError("", "", "", MAPPER.writeValueAsString(param));
				AuditManagerLogger.consoleLoggerError("", "", "",
						"Method name - " + method.getName() + "\n\n" + MAPPER.writeValueAsString(param));
			} catch (JsonProcessingException e) {
				AuditManagerLogger.consoleLoggerError("", "", "", e.getMessage());
			}
		}
	}
}