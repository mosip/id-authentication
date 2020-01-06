package io.mosip.registration.dto;

import lombok.Data;

/**
 * DTO class for User Biometric details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Data
public class UserBiometricDTO {	
	private String usrId;
	private String bioTypeCode;
	private String bioAttributeCode;
}
