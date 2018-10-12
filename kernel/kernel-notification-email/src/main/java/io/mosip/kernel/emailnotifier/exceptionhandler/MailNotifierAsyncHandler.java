package io.mosip.kernel.emailnotifier.exceptionhandler;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.emailnotifier.configuration.LoggerConfiguration;
import io.mosip.kernel.emailnotifier.constants.MailNotifierConstants;
import io.mosip.kernel.emailnotifier.constants.MailNotifierExceptionClassNameConstants;

/**
 * This class handles {@link MailSendException},
 * {@link MailAuthenticationException}, {@link MailException}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@Component
public class MailNotifierAsyncHandler implements AsyncUncaughtExceptionHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler#
	 * handleUncaughtException(java.lang.Throwable, java.lang.reflect.Method,
	 * java.lang.Object[])
	 */
	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		MosipLogger logger = LoggerConfiguration.logConfig(MailNotifierAsyncHandler.class);
		switch (ex.getClass().toString()) {
		case MailNotifierExceptionClassNameConstants.MAIL_AUTH_EXCEPTION_CLASS_NAME:
			logger.error(MailNotifierConstants.EMPTY_STRING.getValue(), MailNotifierConstants.ERROR_CODE.getValue(),
					MailNotifierConstants.MAIL_SEND_EXCEPTION_CODE.getValue(), ex.getMessage());
			break;
		case MailNotifierExceptionClassNameConstants.MAIL_SENDMAIL_SEND_EXCEPTION_CLASS_NAME:
			logger.error(MailNotifierConstants.EMPTY_STRING.getValue(), MailNotifierConstants.ERROR_CODE.getValue(),
					MailNotifierConstants.MAIL_AUTHENTICATION_EXCEPTION_CODE.getValue(), ex.getMessage());
			break;
		default:
			logger.error(MailNotifierConstants.EMPTY_STRING.getValue(), MailNotifierConstants.ERROR_CODE.getValue(),
					MailNotifierConstants.MAIL_EXCEPTION_CODE.getValue(), ex.getMessage());
			break;
		}
	}
}
