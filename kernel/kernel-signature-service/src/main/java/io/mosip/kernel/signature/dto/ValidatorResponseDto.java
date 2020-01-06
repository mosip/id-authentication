package io.mosip.kernel.signature.dto;

import lombok.Data;

/**
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 * 
 */
@Data
public class ValidatorResponseDto {
	/**
	 * The validation request status.
	 */
	private String status;
	/**
	 * The validation request message.
	 */
	private String message;
}
