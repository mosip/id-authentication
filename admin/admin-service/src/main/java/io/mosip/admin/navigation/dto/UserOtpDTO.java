package io.mosip.admin.navigation.dto;

import lombok.Data;

/**
 * @author Ayush Saxena
 *
 */
@Data
public class UserOtpDTO {
	
	private String userId;
	private String otp;
	private String appId;

}
