package io.mosip.kernel.core.notification.spi;

/**
 * This service class send SMS on the contact number provided.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */

public interface SmsNotification<D> {

	/**
	 * This method sends Sms with message provided on the requested number.
	 * 
	 * @param contactNumber
	 *            the number on which Sms needs to be send.
	 * @param contentMessage
	 *            the message provided.
	 * @return the response dto.
	 */
	public D sendSmsNotification(String contactNumber, String contentMessage);

}
