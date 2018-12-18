package io.mosip.authentication.core.dto.otpgen;

import lombok.Data;

/**
 * This class is used to provide request for OTP generation.
 * 
 * @author Dinesh Karuppiah
 *
 */

@Data
public class OtpRequestDTO  {

	private String id;

	//private String ver;

	private String idvId;

	private String idvIdType;

	private String muaCode;

	private String reqTime;

	private String txnID;

}