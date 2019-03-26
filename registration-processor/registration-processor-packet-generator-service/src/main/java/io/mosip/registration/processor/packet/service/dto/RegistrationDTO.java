package io.mosip.registration.processor.packet.service.dto;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.registration.processor.packet.service.dto.demographic.DemographicDTO;

/**
 * This class contains the Registration details.
 * 
 * @author Dinesh Asokan
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegistrationDTO extends BaseDTO {

	private DemographicDTO demographicDTO;
	private String registrationId;
	private String registrationIdHash;

	private RegistrationMetaDataDTO registrationMetaDataDTO;
	private List<AuditDTO> auditDTOs;
	private Timestamp auditLogStartTime;
	private Timestamp auditLogEndTime;

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
	 * @return the auditLogStartTime
	 */
	public Timestamp getAuditLogStartTime() {
		return auditLogStartTime;
	}

	/**
	 * @param auditLogStartTime
	 *            the auditLogStartTime to set
	 */
	public void setAuditLogStartTime(Timestamp auditLogStartTime) {
		this.auditLogStartTime = auditLogStartTime;
	}

	/**
	 * @return the auditLogEndTime
	 */
	public Timestamp getAuditLogEndTime() {
		return auditLogEndTime;
	}

	/**
	 * @param auditLogEndTime
	 *            the auditLogEndTime to set
	 */
	public void setAuditLogEndTime(Timestamp auditLogEndTime) {
		this.auditLogEndTime = auditLogEndTime;
	}

}
