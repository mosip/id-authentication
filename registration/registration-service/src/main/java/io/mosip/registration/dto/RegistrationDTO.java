package io.mosip.registration.dto;

import java.util.List;

import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;

/**
 * This class contains the Registration details.
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class RegistrationDTO extends BaseDTO {
	private BiometricDTO biometricDTO;
	private DemographicDTO demographicDTO;
	private String registrationId;
	private String registrationIdHash;
	private String preRegistrationId;
	private RegistrationMetaDataDTO registrationMetaDataDTO;
	private OSIDataDTO osiDataDTO;
	private List<AuditDTO> auditDTOs;
	private SelectionListDTO selectionListDTO;

	/**
	 * @return the biometricDTO
	 */
	public BiometricDTO getBiometricDTO() {
		return biometricDTO;
	}

	/**
	 * @param biometricDTO
	 *            the biometricDTO to set
	 */
	public void setBiometricDTO(BiometricDTO biometricDTO) {
		this.biometricDTO = biometricDTO;
	}

	/**
	 * @return the demographicDTO
	 */
	public DemographicDTO getDemographicDTO() {
		return demographicDTO;
	}

	/**
	 * @param demographicDTO
	 *            the demographicDTO to set
	 */
	public void setDemographicDTO(DemographicDTO demographicDTO) {
		this.demographicDTO = demographicDTO;
	}

	/**
	 * @return the registrationId
	 */
	public String getRegistrationId() {
		return registrationId;
	}

	/**
	 * @param registrationId
	 *            the registrationId to set
	 */
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public String getRegistrationIdHash() {
		return registrationIdHash;
	}

	public void setRegistrationIdHash(String registrationIdHash) {
		this.registrationIdHash = registrationIdHash;
	}

	/**
	 * @return the preRegistrationId
	 */
	public String getPreRegistrationId() {
		return preRegistrationId;
	}

	/**
	 * @param preRegistrationId
	 *            the preRegistrationId to set
	 */
	public void setPreRegistrationId(String preRegistrationId) {
		this.preRegistrationId = preRegistrationId;
	}

	/**
	 * @return the registrationMetaDataDTO
	 */
	public RegistrationMetaDataDTO getRegistrationMetaDataDTO() {
		return registrationMetaDataDTO;
	}

	/**
	 * @param registrationMetaDataDTO
	 *            the registrationMetaDataDTO to set
	 */
	public void setRegistrationMetaDataDTO(RegistrationMetaDataDTO registrationMetaDataDTO) {
		this.registrationMetaDataDTO = registrationMetaDataDTO;
	}

	/**
	 * @return the osiDataDTO
	 */
	public OSIDataDTO getOsiDataDTO() {
		return osiDataDTO;
	}

	/**
	 * @param osiDataDTO
	 *            the osiDataDTO to set
	 */
	public void setOsiDataDTO(OSIDataDTO osiDataDTO) {
		this.osiDataDTO = osiDataDTO;
	}

	/**
	 * @return the auditDTOs
	 */
	public List<AuditDTO> getAuditDTOs() {
		return auditDTOs;
	}

	/**
	 * @param auditDTOs
	 *            the auditDTOs to set
	 */
	public void setAuditDTOs(List<AuditDTO> auditDTOs) {
		this.auditDTOs = auditDTOs;
	}

	/**
	 * @return the selectionListDTO
	 */
	public SelectionListDTO getSelectionListDTO() {
		return selectionListDTO;
	}

	/**
	 * @param selectionListDTO the selectionListDTO to set
	 */
	public void setSelectionListDTO(SelectionListDTO selectionListDTO) {
		this.selectionListDTO = selectionListDTO;
	}
}
