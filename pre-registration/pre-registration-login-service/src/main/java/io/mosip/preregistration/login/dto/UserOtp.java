package io.mosip.preregistration.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This DTO class is used to define the initial request parameters.
 * 
 * @author Akshay Jain
 * @since 1.0.0
 *
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserOtp {
	private String userId;
	private String otp;
	private String appId;
}
