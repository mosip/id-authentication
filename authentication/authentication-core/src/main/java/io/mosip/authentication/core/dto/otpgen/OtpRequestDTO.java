package io.mosip.authentication.core.dto.otpgen;

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

	// private String ver;
	/** Variable to Individual id */
	private String idvId;

	/** Variable to hold Individual Id Type */
	private String idvIdType;

	/** Variable to hold MUA code */
	private String muaCode;

	/** Variable to hold Request time */
	private String reqTime;

	/** Variable to hold Transaction ID */
	private String txnID;

}