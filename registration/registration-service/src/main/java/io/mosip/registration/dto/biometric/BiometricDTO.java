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
	private BiometricInfoDTO introducerBiometricDTO;
	private BiometricInfoDTO supervisorBiometricDTO;
	private BiometricInfoDTO operatorBiometricDTO;
	public BiometricInfoDTO getApplicantBiometricDTO() {
		return applicantBiometricDTO;
	}
	public void setApplicantBiometricDTO(BiometricInfoDTO applicantBiometricDTO) {
		this.applicantBiometricDTO = applicantBiometricDTO;
	}
	public BiometricInfoDTO getIntroducerBiometricDTO() {
		return introducerBiometricDTO;
	}
	public void setIntroducerBiometricDTO(BiometricInfoDTO introducerBiometricDTO) {
		this.introducerBiometricDTO = introducerBiometricDTO;
	}
	public BiometricInfoDTO getSupervisorBiometricDTO() {
		return supervisorBiometricDTO;
	}
	public void setSupervisorBiometricDTO(BiometricInfoDTO supervisorBiometricDTO) {
		this.supervisorBiometricDTO = supervisorBiometricDTO;
	}
	public BiometricInfoDTO getOperatorBiometricDTO() {
		return operatorBiometricDTO;
	}
	public void setOperatorBiometricDTO(BiometricInfoDTO operatorBiometricDTO) {
		this.operatorBiometricDTO = operatorBiometricDTO;
	}
}
