package io.mosip.authentication.core.dto.indauth;

import lombok.Data;

/**
 * Generic class to find authentication type.
 * For e.g if go for otp validation, then <b>otp</b> attribute set as <b>true</b>
 * @author Rakesh Roshan
 */
@Data
public class AuthTypeDTO {

	/** For id  Authentication */
	private boolean id;
	
	/** For address  Authentication */
	private boolean ad;

	/** For pin  Authentication */
	private boolean pin;

	/** For biometric  Authentication */
	private boolean bio;

	/** For otp  Authentication */
	private boolean otp;

}
