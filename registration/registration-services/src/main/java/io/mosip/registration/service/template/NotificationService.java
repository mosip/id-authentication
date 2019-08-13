package io.mosip.registration.service.template;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * This class is used to send notification service
 * @author Dinesh Ashokan
 *
 */
public interface NotificationService {
	
	/**
	 * This method accepts the message, phone number, registration id to invoke sms service.
	 *
	 * @param message 
	 * 				{@code String} the message to be sent through SMS
	 * @param number 
	 * 				{@code String} the number to which SMS to be sent
	 * @param regId 
	 * 				{@code String} the registration id
	 * 
	 * @return {@code ResponseDTO} based on the result the response DTO will be
	 *         formed and return to the caller.
	 * @throws RegBaseCheckedException 
	 */	
	ResponseDTO sendSMS(String message,String number,String regId) throws RegBaseCheckedException;
	
	/**
	 * This method accept the message and email id, registration id to invoke email service.
	 *
	 * @param message 
	 * 				{@code String} the message to be sent through Email
	 * @param emailId 
	 * 				{@code String} the enail id to which Email to be sent
	 * @param regId 
	 * 				{@code String} the registration id
	 * 
	 * @return 	{@code ResponseDTO} based on the result the response DTO will be
	 *         formed and return to the caller.
	 * @throws RegBaseCheckedException 
	 */
	ResponseDTO sendEmail(String message,String emailId,String regId) throws RegBaseCheckedException;
}
