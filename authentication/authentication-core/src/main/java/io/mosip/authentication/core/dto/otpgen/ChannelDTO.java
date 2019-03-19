package io.mosip.authentication.core.dto.otpgen;

import lombok.Data;

/**
 * This Class Holds values for Email and Phone.
 * 
 * @author Prem Kumar
 *
 */
@Data
public class ChannelDTO {

	/** Variable to hold email */
	private boolean email;

	/** Variable to hold phone */
	private boolean phone;
}
