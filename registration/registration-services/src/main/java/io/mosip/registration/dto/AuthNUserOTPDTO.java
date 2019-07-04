package io.mosip.registration.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The DTO class required for the Authentication Token Web-Service
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthNUserOTPDTO extends AuthNDTO {
	
	private String userId;
	private String otp;
	private String appId;

}
