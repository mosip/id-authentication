package io.mosip.authentication.demo.dto;

import lombok.Data;

/**
 * The Class AuthTypeDTO.
 * 
 * @author Sanjay Murali
 */
@Data
public class AuthTypeDTO {

	/** For demo Authentication */
	private boolean demo;

	/** For biometric Authentication */
	private boolean bio;

	/** For otp Authentication */
	private boolean otp;

	/** For pin Authentication */
	private boolean pin;

}
