package org.mosip.registration.processor.dto.biometric;

import lombok.Data;
/**
 * Exception Iris data and its details
 * @author M1047595
 */
@Data
public class ExceptionIrisDetailsDTO {
	private String missingIris;
	private String exceptionDescription;
	// Permanent or Temporary
	private String exceptionType;

}
