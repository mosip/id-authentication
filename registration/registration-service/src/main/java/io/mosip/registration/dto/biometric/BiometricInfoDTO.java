package io.mosip.registration.dto.biometric;

import java.util.List;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class contains the Biometrics Information namely captured finger-prints,
 * missing finger-prints, captured iris and missing iris
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class BiometricInfoDTO extends BaseDTO {
	// Fingerprint with Exceptions
	private List<FingerprintDetailsDTO> fingerprintDetailsDTO;
	private List<BiometricExceptionDTO> fingerPrintBiometricExceptionDTO;

	// Iris with Exceptions
	private List<IrisDetailsDTO> irisDetailsDTO;
	private int numOfIrisRetry;
	private List<BiometricExceptionDTO> irisBiometricExceptionDTO;
	public List<FingerprintDetailsDTO> getFingerprintDetailsDTO() {
		return fingerprintDetailsDTO;
	}
	public void setFingerprintDetailsDTO(List<FingerprintDetailsDTO> fingerprintDetailsDTO) {
		this.fingerprintDetailsDTO = fingerprintDetailsDTO;
	}
	public List<BiometricExceptionDTO> getFingerPrintBiometricExceptionDTO() {
		return fingerPrintBiometricExceptionDTO;
	}
	public void setFingerPrintBiometricExceptionDTO(List<BiometricExceptionDTO> fingerPrintBiometricExceptionDTO) {
		this.fingerPrintBiometricExceptionDTO = fingerPrintBiometricExceptionDTO;
	}
	public List<IrisDetailsDTO> getIrisDetailsDTO() {
		return irisDetailsDTO;
	}
	public void setIrisDetailsDTO(List<IrisDetailsDTO> irisDetailsDTO) {
		this.irisDetailsDTO = irisDetailsDTO;
	}
	public int getNumOfIrisRetry() {
		return numOfIrisRetry;
	}
	public void setNumOfIrisRetry(int numOfIrisRetry) {
		this.numOfIrisRetry = numOfIrisRetry;
	}
	public List<BiometricExceptionDTO> getIrisBiometricExceptionDTO() {
		return irisBiometricExceptionDTO;
	}
	public void setIrisBiometricExceptionDTO(List<BiometricExceptionDTO> irisBiometricExceptionDTO) {
		this.irisBiometricExceptionDTO = irisBiometricExceptionDTO;
	}	
}
