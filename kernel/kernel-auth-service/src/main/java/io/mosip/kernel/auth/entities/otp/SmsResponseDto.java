package io.mosip.kernel.auth.entities.otp;

import lombok.Data;

/**
 * The DTO class for sms notification response.
 * 
 * @author Ramadurai Pandian
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
