package io.mosip.authentication.core.dto.otpgen;

import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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

	@NotNull
	private String idType;

	@Digits(fraction = 1, integer = 1)
	private String ver;

	@Pattern(regexp = "^[A-Z0-9]{10}")
	private String muaCode;

	@Pattern(regexp = "^[A-Z0-9]{10}")
	private String txnID;

	private Date reqTime;

	@Pattern(regexp = "^[A-Z0-9]{10}")
	private String msaLicenseKey;

}