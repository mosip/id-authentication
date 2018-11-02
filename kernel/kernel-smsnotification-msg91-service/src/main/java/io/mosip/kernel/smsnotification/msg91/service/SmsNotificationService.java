package io.mosip.kernel.smsnotification.msg91.service;

import io.mosip.kernel.smsnotification.msg91.dto.SmsResponseDto;

/**
 * This service class send SMS on the contact number provided.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */

public interface SmsNotificationService {

	/**
	 * SendSmsNotification
	 * 
	 * @param contactNumber
	 * @param contentMessage
	 * @return
	 */
	public SmsResponseDto sendSmsNotification(String contactNumber, String contentMessage);

}
