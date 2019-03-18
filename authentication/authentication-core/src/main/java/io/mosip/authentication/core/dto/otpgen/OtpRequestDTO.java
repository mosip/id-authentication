package io.mosip.authentication.core.dto.otpgen;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * This class is used to provide request for OTP generation.
 * 
 * @author Dinesh Karuppiah
 *
 */

@Data
public class OtpRequestDTO {

	/** Variable to hold id */
	private String id;

	/** Variable to hold version */
	private String version;

	/** Variable to hold Transaction ID */
	private String transactionID;

	/** Variable to hold Request time */
	private String requestTime;

	/** Variable to hold individualID */
	private String individualId;

	/** Variable to hold partnerID */
	private String individualIdType;

	private ChannelDTO otpChannel;

}