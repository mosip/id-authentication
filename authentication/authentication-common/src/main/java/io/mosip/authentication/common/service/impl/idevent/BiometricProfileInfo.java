package io.mosip.authentication.common.service.impl.idevent;

import lombok.Data;
/**
 * Biometric Profile Info used in Authentication Ananymous Profile
 * 
 * @author Loganathan Sekar
 *
 */
@Data
public class BiometricProfileInfo {
	
	private String type;
	
	private String subtype;
	
	private String qualityScore;
	
	private String digitalId;

}
