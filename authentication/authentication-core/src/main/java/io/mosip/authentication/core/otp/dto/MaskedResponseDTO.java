package io.mosip.authentication.core.otp.dto;

import lombok.Data;

/**
 * General-purpose of {@code MaskedResponseDTO} class used to provide
 * MaskedResponse Info's
 * 
 * @author Dinesh Karuppiah.T
 */
@Data
public class MaskedResponseDTO {

	/**
	 * masked mobile(i.e XXXXXXX123) number where send OTP
	 */
	private String maskedMobile;

	/**
	 * masked email id(raXXXXXXXXXan@xyz.com) where send OTP
	 */
	private String maskedEmail;

}
