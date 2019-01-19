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
	private List<BiometricExceptionDTO> biometricExceptionDTO;

	/** The iris details DTO. */
	private List<IrisDetailsDTO> irisDetailsDTO;

	/** The face details DTO. */
	private FaceDetailsDTO faceDetailsDTO;


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
	 * @param fingerprintDetailsDTO the new fingerprint details DTO
	 */
	public void setFingerprintDetailsDTO(List<FingerprintDetailsDTO> fingerprintDetailsDTO) {
		this.fingerprintDetailsDTO = fingerprintDetailsDTO;
	}

	/**
	 * Gets the finger print biometric exception DTO.
	 *
	 * @return the biometric exception DTO
	 */
	public List<BiometricExceptionDTO> getBiometricExceptionDTO() {
		return biometricExceptionDTO;
	}

	/**
	 * Sets the biometric exception DTO.
	 *
	 * @param biometricExceptionDTO the new biometric
	 *                                         exception DTO
	 */
	public void setBiometricExceptionDTO(List<BiometricExceptionDTO> biometricExceptionDTO) {
		this.biometricExceptionDTO = biometricExceptionDTO;
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
	 * @param irisDetailsDTO the new iris details DTO
	 */
	public void setIrisDetailsDTO(List<IrisDetailsDTO> irisDetailsDTO) {
		this.irisDetailsDTO = irisDetailsDTO;
	}

	
	/**
	 * Sets the face details DTO.
	 * 
	 * @return faceDetailsDTO
	 */
	public FaceDetailsDTO getFaceDetailsDTO() {
		return faceDetailsDTO;
	}

	/**
	 * @param faceDetailsDTO the faceDetailsDTO to set
	 */
	public void setFaceDetailsDTO(FaceDetailsDTO faceDetailsDTO) {
		this.faceDetailsDTO = faceDetailsDTO;
	}
}
