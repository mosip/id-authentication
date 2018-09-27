package org.mosip.registration.dto.biometric;

import java.util.List;

import lombok.Data;

@Data
public class BiometricInfoDTO {
	// TODO rename DetailsDTO to DTO
	// Fingerprint with Exceptions and Number of retries
	private List<FingerprintDetailsDTO> fingerprintDetailsDTO;
	private int numOfFingerPrintRetry;
	private List<ExceptionFingerprintDetailsDTO> exceptionFingerprintDetailsDTO;
	
	// Iris with exceptions and number of retries
	private List<IrisDetailsDTO> irisDetailsDTO;
	private int numOfIrisRetry;
	private List<ExceptionIrisDetailsDTO> exceptionIrisDetailsDTO;
}
