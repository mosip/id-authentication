package io.mosip.authentication.core.dto.indauth;

import java.util.Date;

import lombok.Data;

/**
 * {@code AuthResponseInfo} class which contain the response information like
 * {@link AuthResponseInfo#uid},{@link AuthResponseInfo#requestTimeStamp},
 * {@link AuthResponseInfo#apiVersion} and masked mobile number and email-id i.e
 * {@link AuthResponseInfo#maskedMobileNumber},
 * {@link AuthResponseInfo#maskedEmailId}.
 * 
 * Masked mobile number and email id is used for received OTP Masked Mobile:
 * 95XXXXX123 Masked Email: rakXXXXhj@xyz.com
 * 
 * 
 * @author Rakesh Roshan
 */
@Data
public class AuthResponseInfo {

	/**
	 * Type of user ID either UIN or VID
	 */
	private IdType idType;
	/**
	 * Request Time
	 */
	private Date reqTime;
	/**
	 * Version
	 */
	private String ver;
	/**
	 * masked mobile(i.e XXXXXXX123) number where send OTP
	 */
	private String maskedMobile;
	/**
	 * masked email id(raXXXXXXXXXan@xyz.com) where send OTP
	 */
	private String maskedEmail;

}
