package io.mosip.registration.dto.biometric;

import java.util.List;

import io.mosip.registration.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class contains the Biometrics Information namely captured finger-prints,
 * missing finger-prints, captured iris and missing iris
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BiometricInfoDTO extends BaseDTO {
	// Fingerprint with Exceptions
	private List<FingerprintDetailsDTO> fingerprintDetailsDTO;
	private List<BiometricExceptionDTO> fingerPrintBiometricExceptionDTO;

	// Iris with Exceptions
	private List<IrisDetailsDTO> irisDetailsDTO;
	private int numOfIrisRetry;
	private List<BiometricExceptionDTO> irisBiometricExceptionDTO;
}
