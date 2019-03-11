package io.mosip.preregistration.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This DTO class is used to define the initial request parameters.
 * 
 * @author Akshay Jain
 * @since 1.0.0
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class UserOtp {
	private String userId;
	private String otp;
	private String appId;
}
