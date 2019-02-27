package io.mosip.authentication.core.dto.otpgen;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 */
@Data
public class ChannelDTO {
	
	/** Variable to hold email*/
	private String email;
	
	/** Variable to hold phone */
	private String phone;
}
