package org.mosip.registration.processor.dto.biometric;

import lombok.Data;

/**
 * Exception fingerprint data and its details
 * @author M1047595
 *
 */
@Data
public class ExceptionFingerprintDetailsDTO {
	
	private String missingFinger;
	private String exceptionDescription;
	// Permanent or Temporary
	private String exceptionType;
	
}