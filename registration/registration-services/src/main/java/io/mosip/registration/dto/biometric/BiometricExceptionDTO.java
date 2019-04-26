package io.mosip.registration.dto.biometric;

import io.mosip.registration.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * This class contains the information on exception fingerprint data
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
@Getter
@Setter
public class BiometricExceptionDTO extends BaseDTO {
	// Fingerprint or Iris
	protected String biometricType;
	protected String missingBiometric;
	protected String reason;
	// Permanent or Temporary
	protected String exceptionType;
	private boolean isMarkedAsException;
	private String individualType;
}