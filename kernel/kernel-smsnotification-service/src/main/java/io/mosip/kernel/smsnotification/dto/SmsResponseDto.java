package io.mosip.kernel.smsnotification.dto;

import lombok.Data;

/**
 * The DTO class for sms notification response.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
public class SmsResponseDto {

	/**
	 * Response status.
	 */
	private String status;
	
	/**
	 * Response message
	 */
	private String message;
}
