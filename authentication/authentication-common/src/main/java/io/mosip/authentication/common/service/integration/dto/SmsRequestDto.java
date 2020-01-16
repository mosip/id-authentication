package io.mosip.authentication.common.service.integration.dto;

import lombok.Data;

/**
 * General-purpose of {@code SmsRequestDto} class used to store Sms request
 * details
 * 
 * @author Dinesh Karuppiah.T
 */
@Data
public class SmsRequestDto {

	/**
	 * variable to hole message
	 */
	String message;
	/**
	 * variable to hole number
	 */
	String number;
}
