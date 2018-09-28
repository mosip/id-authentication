package org.mosip.registration.dto.biometric;

import java.util.List;

import org.mosip.registration.dto.BaseDTO;

/**
 * This class contains the Biometrics Information namely captured finger-prints,
 * missing finger-prints, captured iris and missing iris
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class BiometricInfoDTO extends BaseDTO {
	// Fingerprint with Exceptions and Number of retries
	private List<FingerprintDetailsDTO> fingerprintDetailsDTO;
	private int numOfFingerPrintRetry;
	private List<ExceptionFingerprintDetailsDTO> exceptionFingerprintDetailsDTO;

	// Iris with exceptions and number of retries
	private List<IrisDetailsDTO> irisDetailsDTO;
	private int numOfIrisRetry;
	private List<ExceptionIrisDetailsDTO> exceptionIrisDetailsDTO;

	/**
	 * @return the fingerprintDetailsDTO
	 */
	public List<FingerprintDetailsDTO> getFingerprintDetailsDTO() {
		return fingerprintDetailsDTO;
	}

	/**
	 * @param fingerprintDetailsDTO
	 *            the fingerprintDetailsDTO to set
	 */
	public void setFingerprintDetailsDTO(List<FingerprintDetailsDTO> fingerprintDetailsDTO) {
		this.fingerprintDetailsDTO = fingerprintDetailsDTO;
	}

	/**
	 * @return the numOfFingerPrintRetry
	 */
	public int getNumOfFingerPrintRetry() {
		return numOfFingerPrintRetry;
	}

	/**
	 * @param numOfFingerPrintRetry
	 *            the numOfFingerPrintRetry to set
	 */
	public void setNumOfFingerPrintRetry(int numOfFingerPrintRetry) {
		this.numOfFingerPrintRetry = numOfFingerPrintRetry;
	}

	/**
	 * @return the exceptionFingerprintDetailsDTO
	 */
	public List<ExceptionFingerprintDetailsDTO> getExceptionFingerprintDetailsDTO() {
		return exceptionFingerprintDetailsDTO;
	}

	/**
	 * @param exceptionFingerprintDetailsDTO
	 *            the exceptionFingerprintDetailsDTO to set
	 */
	public void setExceptionFingerprintDetailsDTO(List<ExceptionFingerprintDetailsDTO> exceptionFingerprintDetailsDTO) {
		this.exceptionFingerprintDetailsDTO = exceptionFingerprintDetailsDTO;
	}

	/**
	 * @return the irisDetailsDTO
	 */
	public List<IrisDetailsDTO> getIrisDetailsDTO() {
		return irisDetailsDTO;
	}

	/**
	 * @param irisDetailsDTO
	 *            the irisDetailsDTO to set
	 */
	public void setIrisDetailsDTO(List<IrisDetailsDTO> irisDetailsDTO) {
		this.irisDetailsDTO = irisDetailsDTO;
	}

	/**
	 * @return the numOfIrisRetry
	 */
	public int getNumOfIrisRetry() {
		return numOfIrisRetry;
	}

	/**
	 * @param numOfIrisRetry
	 *            the numOfIrisRetry to set
	 */
	public void setNumOfIrisRetry(int numOfIrisRetry) {
		this.numOfIrisRetry = numOfIrisRetry;
	}

	/**
	 * @return the exceptionIrisDetailsDTO
	 */
	public List<ExceptionIrisDetailsDTO> getExceptionIrisDetailsDTO() {
		return exceptionIrisDetailsDTO;
	}

	/**
	 * @param exceptionIrisDetailsDTO
	 *            the exceptionIrisDetailsDTO to set
	 */
	public void setExceptionIrisDetailsDTO(List<ExceptionIrisDetailsDTO> exceptionIrisDetailsDTO) {
		this.exceptionIrisDetailsDTO = exceptionIrisDetailsDTO;
	}
}
