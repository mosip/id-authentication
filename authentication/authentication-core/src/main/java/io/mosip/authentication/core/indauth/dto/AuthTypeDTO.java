package io.mosip.authentication.core.indauth.dto;

import lombok.Data;

/**
 * Generic class to find authentication type. For e.g if go for otp validation,
 * then <b>otp</b> attribute set as <b>true</b>
 * 
 * @author Rakesh Roshan
 * @author Dinesh Karuppiah.T
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
