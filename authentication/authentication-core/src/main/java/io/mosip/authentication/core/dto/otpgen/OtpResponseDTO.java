package io.mosip.authentication.core.dto.otpgen;

import java.util.Date;
import java.util.List;

import io.mosip.authentication.core.dto.indauth.AuthError;
import lombok.Data;

/**
 * This class is used to provide response for OTP generation.
 * 
 * @author Dinesh Karuppiah
 *
 */

@Data
public class OtpResponseDTO {

	private String status;
	private List<AuthError> errorCode;
	private String txnID;
	private Date responseTime;
	
	/**
	 * masked mobile(i.e XXXXXXX123) number where send OTP
	 */
	//TODO
	private String maskedMobile;
	/**
	 * masked email id(raXXXXXXXXXan@xyz.com) where send OTP
	 */
	//TODO
	private String maskedEmail;

}
