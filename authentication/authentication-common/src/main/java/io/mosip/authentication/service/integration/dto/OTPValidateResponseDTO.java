package io.mosip.authentication.service.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
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
