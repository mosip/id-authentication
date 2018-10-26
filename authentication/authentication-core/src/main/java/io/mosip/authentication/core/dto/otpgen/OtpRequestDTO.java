package io.mosip.authentication.core.dto.otpgen;

import java.util.Date;

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

	private String idType;

	private String ver;

	private String muaCode;

	private String txnID;

	private Date reqTime;

	private String msaLicenseKey;

}