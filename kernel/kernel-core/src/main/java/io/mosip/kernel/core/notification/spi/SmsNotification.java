package io.mosip.kernel.core.notification.spi;

/**
 * This service class send SMS on the contact number provided.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */

public interface SmsNotification<D> {


	public D sendSmsNotification(String contactNumber, String contentMessage);

}
