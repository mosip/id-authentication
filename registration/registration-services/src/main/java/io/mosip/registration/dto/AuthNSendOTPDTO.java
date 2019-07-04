package io.mosip.registration.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The DTO class required to send the OTP
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthNSendOTPDTO extends AuthNDTO {

	private String userId;
	private String langCode;
	private List<String> otpChannel;
	private String appId;
	private String useridtype;
	private String context;
	private List<Object> templateVariables;

}
