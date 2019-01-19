package io.mosip.kernel.emailnotification.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for {@link NotificationException}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
public class NotificationException extends BaseUncheckedException {

	/**
	 * Generated serial version.
	 */
	private static final long serialVersionUID = 3949838534862481500L;

	/**
	 * Constructor for NotificationException.
	 * 
	 * @param notificationException
	 *            the exception argument.
	 */
	public NotificationException(Throwable notificationException) {
		super(notificationException.getLocalizedMessage(), notificationException.getMessage());
	}
}
