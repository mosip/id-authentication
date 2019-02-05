package io.mosip.preregistration.notification.dto;

import lombok.Data;

/**
 * DTO class for sms request.
 * 
 * @author Sanober Noor
 * @since 1.0.0
 */
@Data
public class SMSRequestDTO {
	/**
	 * The message.
	 */
	private String message;
	/**
	 * The number.
	 */
	private String number;
}
