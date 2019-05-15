package io.mosip.kernel.auth.dto.otp;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class OtpUser {
	private String userId;
	private List<String> otpChannel;
	private String appId;
	private String useridtype;
	private Map<String,Object> templateVariables;
	private String context;
}
