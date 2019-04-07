package io.mosip.authentication.service.integration.dto;

import lombok.Data;

/**
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
