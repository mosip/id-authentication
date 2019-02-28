package io.mosip.kernel.auth.entities.otp;

import lombok.Data;

@Data
public class OtpUser{
	private String userId;
    private String langCode;
    private String otpChannel;
    private String appId;
}
