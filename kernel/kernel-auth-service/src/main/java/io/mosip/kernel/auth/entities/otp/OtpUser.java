package io.mosip.kernel.auth.entities.otp;

import java.util.List;

import lombok.Data;

@Data
public class OtpUser {
	private String userId;
	private String langCode;
	private List<String> otpChannel;
	private String appId;
	private String useridtype;
}
