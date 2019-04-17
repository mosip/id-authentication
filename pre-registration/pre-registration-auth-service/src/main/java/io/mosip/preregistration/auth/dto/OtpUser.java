package io.mosip.preregistration.auth.dto;

import java.util.List;

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
public class OtpUser {
	private String userId;
    private String langCode;
    private List<String> otpChannel;
    private String appId;
    private String useridtype; 
}
