package io.mosip.registration.dto.biometric;

import java.util.List;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class contains the Biometrics Information namely captured finger-prints,
 * missing finger-prints, captured iris and missing iris.
 *
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class BiometricInfoDTO extends BaseDTO {

	/** The fingerprint details DTO. */
	private List<FingerprintDetailsDTO> fingerprintDetailsDTO;

	/** The finger print biometric exception DTO. */
	private List<BiometricExceptionDTO> fingerPrintBiometricExceptionDTO;

	/** The iris details DTO. */
	private List<IrisDetailsDTO> irisDetailsDTO;

	/** The iris biometric exception DTO. */
	private List<BiometricExceptionDTO> irisBiometricExceptionDTO;

	/**
	 * Gets the fingerprint details DTO.
	 *
	 * @return the fingerprint details DTO
	 */
	public List<FingerprintDetailsDTO> getFingerprintDetailsDTO() {
		return fingerprintDetailsDTO;
	}

	/**
	 * Sets the fingerprint details DTO.
	 *
	 * @param fingerprintDetailsDTO
	 *            the new fingerprint details DTO
	 */
	public void setFingerprintDetailsDTO(List<FingerprintDetailsDTO> fingerprintDetailsDTO) {
		this.fingerprintDetailsDTO = fingerprintDetailsDTO;
	}

	/**
	 * Gets the finger print biometric exception DTO.
	 *
	 * @return the finger print biometric exception DTO
	 */
	public List<BiometricExceptionDTO> getFingerPrintBiometricExceptionDTO() {
		return fingerPrintBiometricExceptionDTO;
	}

	/**
	 * Sets the finger print biometric exception DTO.
	 *
	 * @param fingerPrintBiometricExceptionDTO
	 *            the new finger print biometric exception DTO
	 */
	public void setFingerPrintBiometricExceptionDTO(List<BiometricExceptionDTO> fingerPrintBiometricExceptionDTO) {
		this.fingerPrintBiometricExceptionDTO = fingerPrintBiometricExceptionDTO;
	}

	/**
	 * Gets the iris details DTO.
	 *
	 * @return the iris details DTO
	 */
	public List<IrisDetailsDTO> getIrisDetailsDTO() {
		return irisDetailsDTO;
	}

	/**
	 * Sets the iris details DTO.
	 *
	 * @param irisDetailsDTO
	 *            the new iris details DTO
	 */
	public void setIrisDetailsDTO(List<IrisDetailsDTO> irisDetailsDTO) {
		this.irisDetailsDTO = irisDetailsDTO;
	}

	/**
	 * Gets the iris biometric exception DTO.
	 *
	 * @return the iris biometric exception DTO
	 */
	public List<BiometricExceptionDTO> getIrisBiometricExceptionDTO() {
		return irisBiometricExceptionDTO;
	}

	/**
	 * Sets the iris biometric exception DTO.
	 *
	 * @param irisBiometricExceptionDTO
	 *            the new iris biometric exception DTO
	 */
	public void setIrisBiometricExceptionDTO(List<BiometricExceptionDTO> irisBiometricExceptionDTO) {
		this.irisBiometricExceptionDTO = irisBiometricExceptionDTO;
	}
}
