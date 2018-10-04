/**
 * 
 */
package io.mosip.registration.processor.status.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.mosip.registration.processor.status.code.SyncStatus;
import io.mosip.registration.processor.status.code.SyncType;

/**
 * The Class SyncRegistrationDto.
 *
 * @author M1047487
 */
public class SyncRegistrationDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3922338139042373367L;

	/** The Sync registration id. */
	private String syncRegistrationId;

	/** The registration id. */
	private String registrationId;
	
	/** The registration type. */
	private String registrationType;
	
	/** The parent registration id. */
	private String parentRegistrationId;
	
	/** The status code. */
	private String statusCode;
	
	/** The status comment. */
	private String statusComment;
	
	/** The lang code. */
	private String langCode;
	
	/** The is active. */
	private Boolean isActive;
	
	/** The created by. */
	private String createdBy;
	
	/** The create date time. */
	private LocalDateTime createDateTime;
	
	/** The updated by. */
	private String updatedBy;
	
	/** The update date time. */
	private LocalDateTime updateDateTime;
	
	/** The is deleted. */
	private Boolean isDeleted;
	
	/** The deleted date time. */
	private LocalDateTime deletedDateTime;
	
	/**
	 * Instantiates a new sync registration dto.
	 */
	public SyncRegistrationDto() {
		super();
	}

	/**
	 * Instantiates a new sync registration dto.
	 *
	 * @param registrationId the registration id
	 * @param parentRegistrationId the parent registration id
	 * @param syncType            the sync type
	 * @param synchStatus            the synch status
	 */
	public SyncRegistrationDto(String registrationId, String parentRegistrationId, SyncType syncType, SyncStatus synchStatus) {

		this.registrationId = registrationId;
		this.parentRegistrationId = parentRegistrationId;

	}

	/**
	 * Gets the sync registration id.
	 *
	 * @return the sync registration id
	 */
	public String getSyncRegistrationId() {
		return syncRegistrationId;
	}

	/**
	 * Sets the sync registration id.
	 *
	 * @param syncRegistrationId the new sync registration id
	 */
	public void setSyncRegistrationId(String syncRegistrationId) {
		this.syncRegistrationId = syncRegistrationId;
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
	 * @param registrationId the new registration id
	 */
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	/**
	 * Gets the registration type.
	 *
	 * @return the registration type
	 */
	public String getRegistrationType() {
		return registrationType;
	}

	/**
	 * Sets the registration type.
	 *
	 * @param registrationType the new registration type
	 */
	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}

	/**
	 * Gets the parent registration id.
	 *
	 * @return the parent registration id
	 */
	public String getParentRegistrationId() {
		return parentRegistrationId;
	}

	/**
	 * Sets the parent registration id.
	 *
	 * @param parentRegistrationId the new parent registration id
	 */
	public void setParentRegistrationId(String parentRegistrationId) {
		this.parentRegistrationId = parentRegistrationId;
	}

	/**
	 * Gets the status code.
	 *
	 * @return the status code
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the status code.
	 *
	 * @param statusCode the new status code
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the status comment.
	 *
	 * @return the status comment
	 */
	public String getStatusComment() {
		return statusComment;
	}

	/**
	 * Sets the status comment.
	 *
	 * @param statusComment the new status comment
	 */
	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
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
	 * @param langCode the new lang code
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
	 * @param isActive the new checks if is active
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Gets the created by.
	 *
	 * @return the created by
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Sets the created by.
	 *
	 * @param createdBy the new created by
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Gets the creates the date time.
	 *
	 * @return the creates the date time
	 */
	public LocalDateTime getCreateDateTime() {
		return createDateTime;
	}

	/**
	 * Sets the creates the date time.
	 *
	 * @param createDateTime the new creates the date time
	 */
	public void setCreateDateTime(LocalDateTime createDateTime) {
		this.createDateTime = createDateTime;
	}

	/**
	 * Gets the updated by.
	 *
	 * @return the updated by
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * Sets the updated by.
	 *
	 * @param updatedBy the new updated by
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * Gets the update date time.
	 *
	 * @return the update date time
	 */
	public LocalDateTime getUpdateDateTime() {
		return updateDateTime;
	}

	/**
	 * Sets the update date time.
	 *
	 * @param updateDateTime the new update date time
	 */
	public void setUpdateDateTime(LocalDateTime updateDateTime) {
		this.updateDateTime = updateDateTime;
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
	 * @param isDeleted the new checks if is deleted
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * Gets the deleted date time.
	 *
	 * @return the deleted date time
	 */
	public LocalDateTime getDeletedDateTime() {
		return deletedDateTime;
	}

	/**
	 * Sets the deleted date time.
	 *
	 * @param deletedDateTime the new deleted date time
	 */
	public void setDeletedDateTime(LocalDateTime deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}

}
