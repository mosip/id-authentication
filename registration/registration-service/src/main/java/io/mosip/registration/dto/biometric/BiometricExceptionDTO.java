package io.mosip.registration.dto.biometric;

import io.mosip.registration.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class contains the information on exception fingerprint data
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BiometricExceptionDTO extends BaseDTO {
	// Fingerprint or Iris
	protected String biometricType;
	protected String missingBiometric;
	protected String exceptionDescription;
	// Permanent or Temporary
	protected String exceptionType;

}