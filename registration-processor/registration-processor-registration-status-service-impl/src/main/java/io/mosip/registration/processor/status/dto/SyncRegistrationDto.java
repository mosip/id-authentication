/**
 * 
 */
package io.mosip.registration.processor.status.dto;

import java.io.Serializable;
import java.math.BigInteger;

import org.json.simple.JSONArray;

import io.swagger.annotations.ApiModelProperty;

// TODO: Auto-generated Javadoc
/**
 * The Class SyncRegistrationDto.
 *
 * @author M1047487
 * @author Girish Yarru
 */
public class SyncRegistrationDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3922338139042373367L;

	/** The registration id. */
	private String registrationId;

	/** The sync type dto. */
	private String registrationType = SyncTypeDto.NEW.getValue();

	/** The lang code. */
	private String packetHashValue;

	/** The lang code. */
	private BigInteger packetSize;

	/** The status code. */
	private String supervisorStatus;

	/** The status comment. */
	private String supervisorComment;

	/** The optional values. */
	private JSONArray optionalValues;

	/** The lang code. */
	private String langCode;

	/** The is active. */
	@ApiModelProperty(hidden = true)
	private Boolean isActive;

	/** The is deleted. */
	@ApiModelProperty(hidden = true)
	private Boolean isDeleted;

	/**
	 * Instantiates a new sync registration dto.
	 */
	public SyncRegistrationDto() {
		super();
	}

	/**
	 * Instantiates a new sync registration dto.
	 *
	 * @param registrationId
	 *            the registration id
	 * @param syncTypeDto
	 *            the sync type dto
	 * @param syncStatusDto
	 *            the sync status dto
	 * @param statusComment
	 *            the status comment
	 * @param langCode
	 *            the lang code
	 */
	public SyncRegistrationDto(String registrationId, String syncTypeDto, SyncStatusDto syncStatusDto,
			String statusComment, String langCode) {
		super();
		this.registrationId = registrationId;
		this.registrationType = syncTypeDto;
		this.langCode = langCode;
	}

	/**
	 * Gets the registration id.
	 *
	 * @return the registration id
	 */
	public String getRegistrationId() {
		return registrationId;
	}

	/**
	 * Sets the registration id.
	 *
	 * @param registrationId
	 *            the new registration id
	 */
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	/**
	 * Gets the lang code.
	 *
	 * @return the lang code
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * Sets the lang code.
	 *
	 * @param langCode
	 *            the new lang code
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * Gets the checks if is active.
	 *
	 * @return the checks if is active
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * Sets the checks if is active.
	 *
	 * @param isActive
	 *            the new checks if is active
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Gets the sync type dto.
	 *
	 * @return the sync type dto
	 */
	public String getRegistrationType() {
		return registrationType;
	}

	/**
	 * Sets the sync type dto.
	 *
	 * @param syncTypeDto
	 *            the new sync type
	 */
	public void setSyncType(String syncTypeDto) {
		this.registrationType = syncTypeDto;
	}

	/**
	 * 
	 * 
	 * /** Gets the packet size.
	 *
	 * @return the packet size
	 */
	public BigInteger getPacketSize() {
		return packetSize;
	}

	/**
	 * Sets the packet size.
	 *
	 * @param packetSize
	 *            the new packet size
	 */
	public void setPacketSize(BigInteger packetSize) {
		this.packetSize = packetSize;
	}

	/**
	 * Gets the packet hash value.
	 *
	 * @return the packet hash value
	 */
	public String getPacketHashValue() {
		return packetHashValue;
	}

	/**
	 * Sets the packet hash value.
	 *
	 * @param packetHashValue
	 *            the new packet hash value
	 */
	public void setPacketHashValue(String packetHashValue) {
		this.packetHashValue = packetHashValue;
	}

	/**
	 * Gets the supervisor status.
	 *
	 * @return the supervisor status
	 */
	public String getSupervisorStatus() {
		return supervisorStatus;
	}

	/**
	 * Sets the supervisor status.
	 *
	 * @param supervisorStatus
	 *            the new supervisor status
	 */
	public void setSupervisorStatus(String supervisorStatus) {
		this.supervisorStatus = supervisorStatus;
	}

	/**
	 * Gets the supervisor comment.
	 *
	 * @return the supervisor comment
	 */
	public String getSupervisorComment() {
		return supervisorComment;
	}

	/**
	 * Sets the supervisor comment.
	 *
	 * @param supervisorComment
	 *            the new supervisor comment
	 */
	public void setSupervisorComment(String supervisorComment) {
		this.supervisorComment = supervisorComment;
	}

	/**
	 * Gets the optional values.
	 *
	 * @return the optional values
	 */
	public JSONArray getOptionalValues() {
		return optionalValues;
	}

	/**
	 * Sets the optional values.
	 *
	 * @param optionalValues
	 *            the new optional values
	 */
	public void setOptionalValues(JSONArray optionalValues) {
		this.optionalValues = optionalValues;
	}

	/**
	 * Gets the checks if is deleted.
	 *
	 * @return the checks if is deleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * Sets the checks if is deleted.
	 *
	 * @param isDeleted
	 *            the new checks if is deleted
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
