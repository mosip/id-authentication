package io.mosip.kernel.emailnotification.constant;

import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;

/**
 * Class that provides with the constants for mail notifier exceptions that are
 * not handled by controller advice.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
public final class MailNotifierExceptionClassNameConstants {
	/**
	 * Private constructor for {@link MailNotifierExceptionClassNameConstants}
	 */
	private MailNotifierExceptionClassNameConstants() {
	}

	/**
	 * Constant for {@link MailSendException}
	 */
	public static final String MAIL_SENDMAIL_SEND_EXCEPTION_CLASS_NAME = "class org.springframework.mail.MailSendException";
	/**
	 * Constant for {@link MailAuthenticationException}
	 */
	public static final String MAIL_AUTH_EXCEPTION_CLASS_NAME = "class org.springframework.mail.MailAuthenticationException";
}
