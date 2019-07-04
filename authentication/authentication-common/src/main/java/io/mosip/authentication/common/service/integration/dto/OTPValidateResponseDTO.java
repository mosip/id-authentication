package io.mosip.authentication.common.service.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * General-purpose of {@code OTPValidateResponseDTO} class used to store Otp
 * Validation response details
 * 
 * @author Dinesh Karuppiah
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTPValidateResponseDTO {
	/**
	 * Variable to hold the otp response status.
	 */
	private String status;

	/**
	 * Variable to hold the otp response message.
	 */
	private String message;

}
