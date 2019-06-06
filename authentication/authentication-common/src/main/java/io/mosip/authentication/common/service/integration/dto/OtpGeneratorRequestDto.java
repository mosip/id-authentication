package io.mosip.authentication.common.service.integration.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * The DTO class for generating an OTP.
 * 
 * @author Rakesh Roshan
 */
@Data
public class OtpGeneratorRequestDto {
	private String userId;
	private List<String> otpChannel;
	private String appId;
	private String useridtype;
	private Map<String, Object> templateVariables;
	private String context;
}
