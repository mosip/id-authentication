package io.mosip.registration.service;

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
	 */	
	void sendSMS(String message,String number);
	
	/**
	 * This method accept the message and email id to send email service
	 * @param message
	 * @param email
	 */
	void sendEmail(String message,String emailId);
}
