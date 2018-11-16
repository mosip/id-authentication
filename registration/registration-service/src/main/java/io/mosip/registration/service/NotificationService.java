package io.mosip.registration.service;

import io.mosip.registration.dto.ResponseDTO;

/**
 * This class is used to send notification service
 * @author Dinesh Ashokan
 *
 */
public interface NotificationService {
	
	/**
	 * This method accepts the message and phone number to send sms service
	 * @param message
	 * @param number
	 * @return
	 * 		response for the sms notification
	 */	
	ResponseDTO sendSMS(String message,String number);
	
	/**
	 * This method accept the message and email id to send email service
	 * @param message
	 * @param email
	 * @return
	 * 		response for the email notification
	 */
	ResponseDTO sendEmail(String message,String emailId);
}
