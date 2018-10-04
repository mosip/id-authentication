package io.mosip.registration.dto.biometric;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class contains the Biometric details
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class BiometricDTO extends BaseDTO {
	private BiometricInfoDTO applicantBiometricDTO;
	private BiometricInfoDTO hofBiometricDTO;
	private BiometricInfoDTO introducerBiometricDTO;
	private BiometricInfoDTO supervisorBiometricDTO;
	private BiometricInfoDTO operatorBiometricDTO;

	/**
	 * @return the applicantBiometricDTO
	 */
	public BiometricInfoDTO getApplicantBiometricDTO() {
		return applicantBiometricDTO;
	}

	/**
	 * @param applicantBiometricDTO
	 *            the applicantBiometricDTO to set
	 */
	public void setApplicantBiometricDTO(BiometricInfoDTO applicantBiometricDTO) {
		this.applicantBiometricDTO = applicantBiometricDTO;
	}

	/**
	 * @return the hofBiometricDTO
	 */
	public BiometricInfoDTO getHofBiometricDTO() {
		return hofBiometricDTO;
	}

	/**
	 * @param hofBiometricDTO
	 *            the hofBiometricDTO to set
	 */
	public void setHofBiometricDTO(BiometricInfoDTO hofBiometricDTO) {
		this.hofBiometricDTO = hofBiometricDTO;
	}

	/**
	 * @return the introducerBiometricDTO
	 */
	public BiometricInfoDTO getIntroducerBiometricDTO() {
		return introducerBiometricDTO;
	}

	/**
	 * @param introducerBiometricDTO
	 *            the introducerBiometricDTO to set
	 */
	public void setIntroducerBiometricDTO(BiometricInfoDTO introducerBiometricDTO) {
		this.introducerBiometricDTO = introducerBiometricDTO;
	}

	/**
	 * @return the supervisorBiometricDTO
	 */
	public BiometricInfoDTO getSupervisorBiometricDTO() {
		return supervisorBiometricDTO;
	}

	/**
	 * @param supervisorBiometricDTO
	 *            the supervisorBiometricDTO to set
	 */
	public void setSupervisorBiometricDTO(BiometricInfoDTO supervisorBiometricDTO) {
		this.supervisorBiometricDTO = supervisorBiometricDTO;
	}

	/**
	 * @return the operatorBiometricDTO
	 */
	public BiometricInfoDTO getOperatorBiometricDTO() {
		return operatorBiometricDTO;
	}

	/**
	 * @param operatorBiometricDTO
	 *            the operatorBiometricDTO to set
	 */
	public void setOperatorBiometricDTO(BiometricInfoDTO operatorBiometricDTO) {
		this.operatorBiometricDTO = operatorBiometricDTO;
	}
}
