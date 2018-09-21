package org.mosip.auth.core.dto.indauth;

import java.io.Serializable;

import lombok.Data;

/**
 *  {@code OtpAuthRequestDTO} is the DTO class for the OTP Authentication Request.
 *
 * @author Mahesh Kumar
 */
@Data
public class OtpAuthRequestDTO implements Serializable{

	private static final long serialVersionUID = 8520156454491256527L;
	
	private AuthRequestDTO authReq;
	private OtpRequestDTO otpReq;
	
	
}
