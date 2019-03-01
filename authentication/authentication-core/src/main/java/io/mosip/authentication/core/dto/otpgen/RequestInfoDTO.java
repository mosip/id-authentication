package io.mosip.authentication.core.dto.otpgen;

import lombok.Data;

/**
 * Class to hold OtpIdentityDTO and ChannelDTO
 * 
 * @author Prem Kumar
 *
 */
@Data
public class RequestInfoDTO {

	/** Variable to hold OtpIdentityDTO */
	private OtpIdentityDTO identity;

	/** Variable to hold ChannelDTO */
	private ChannelDTO channel;
}
