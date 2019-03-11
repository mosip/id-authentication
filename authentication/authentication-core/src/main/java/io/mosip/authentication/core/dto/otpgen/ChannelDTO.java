package io.mosip.authentication.core.dto.otpgen;

import lombok.Data;

/**
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
