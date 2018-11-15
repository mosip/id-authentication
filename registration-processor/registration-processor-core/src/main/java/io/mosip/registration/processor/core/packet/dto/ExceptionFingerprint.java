package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new exception fingerprint.
 */
@Data
public class ExceptionFingerprint {

	/** The biometric type. */
	private String biometricType;
	
	/** The missing biometric. */
	private String missingBiometric;
	
	/** The exception description. */
	private String exceptionDescription;
	
	/** The exception type. */
	private String exceptionType;

}