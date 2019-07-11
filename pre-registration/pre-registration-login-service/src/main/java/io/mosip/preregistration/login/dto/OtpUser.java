package io.mosip.preregistration.login.dto;

import java.util.List;
import java.util.Map;

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
public class OtpUser {
	private String userId;
	private List<String> otpChannel;
	private String appId;
	private String useridtype;
	private Map<String,Object> templateVariables;
	private String context; 
}
