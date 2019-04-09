package io.mosip.kernel.auth.entities;

import lombok.Data;

@Data
public class UserOtp {

	private String userId;
	private String otp;
	private String appId;

}
