package io.mosip.authentication.common.service.integration.dto;

import lombok.Data;

/**
 * General-purpose of {@code SmsRequestDto} class used to store Sms response
 * details
 * 
 * @author Dinesh Karuppiah.T
 */
@Data
public class SmsResponseDto {

	/**
	 * variable to hole message
	 */
	String message;

	/**
	 * variable to hole status
	 */
	String status;

}
