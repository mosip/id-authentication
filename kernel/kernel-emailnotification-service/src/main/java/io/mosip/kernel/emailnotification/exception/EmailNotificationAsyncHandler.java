package io.mosip.kernel.emailnotification.exception;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.emailnotification.config.LoggerConfiguration;
import io.mosip.kernel.emailnotification.constant.MailNotifierArgumentErrorConstants;
import io.mosip.kernel.emailnotification.constant.MailNotifierConstants;
import io.mosip.kernel.emailnotification.constant.MailNotifierExceptionClassNameConstants;

/**
 * This class handles {@link MailSendException},
 * {@link MailAuthenticationException}, {@link MailException}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@Component
public class EmailNotificationAsyncHandler implements AsyncUncaughtExceptionHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler#
	 * handleUncaughtException(java.lang.Throwable, java.lang.reflect.Method,
	 * java.lang.Object[])
	 */
	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		Logger mosipLogger = LoggerConfiguration.logConfig(EmailNotificationAsyncHandler.class);
		switch (ex.getClass().toString()) {
		case MailNotifierExceptionClassNameConstants.MAIL_AUTH_EXCEPTION_CLASS_NAME:
			mosipLogger.error(MailNotifierConstants.EMPTY_STRING.getValue(), MailNotifierConstants.ERROR_CODE.getValue(),
					MailNotifierArgumentErrorConstants.MAIL_AUTHENTICATION_EXCEPTION_CODE.getErrorCode()
					, ex.getMessage());
			break;
		case MailNotifierExceptionClassNameConstants.MAIL_SENDMAIL_SEND_EXCEPTION_CLASS_NAME:
			mosipLogger.error(MailNotifierConstants.EMPTY_STRING.getValue(), MailNotifierConstants.ERROR_CODE.getValue(),
					MailNotifierArgumentErrorConstants.MAIL_SEND_EXCEPTION_CODE.getErrorCode(),
					ex.getMessage());
			break;
		default:
			mosipLogger.error(MailNotifierConstants.EMPTY_STRING.getValue(), MailNotifierConstants.ERROR_CODE.getValue(),
					MailNotifierArgumentErrorConstants.MAIL_EXCEPTION_CODE.getErrorCode(), ex.getMessage());
			break;
		}
	}
}
