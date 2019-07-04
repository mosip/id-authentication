package io.mosip.kernel.smsnotification.dto;

import lombok.Data;

/**
 * The DTO class for sms server response.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
public class SmsServerResponseDto {
	/**
	 * Status for request.
	 */
	private String type;

	/**
	 * Response message
	 */
	private String message;

	/**
	 * Response code.
	 */
	private String code;
}
