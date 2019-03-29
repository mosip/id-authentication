package io.mosip.registration.processor.packet.service.dto;

/**
 * The Class SyncRegistrationDTO.
 * 
 * @author Rishabh Keshari
 */
public class SyncRegistrationDTO {

	/** The lang code. */
	private String langCode;

	/** The parent registration id. */
	private String parentRegistrationId;

	/** The registration id. */
	private String registrationId;

	/** The status comment. */
	private String statusComment;

	/** The sync status. */
	private String syncStatus;

	/** The sync type. */
	private String syncType;

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
	 * @param parentRegistrationId
	 *            the new parent registration id
	 */
	public void setParentRegistrationId(String parentRegistrationId) {
		this.parentRegistrationId = parentRegistrationId;
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
	 * @param statusComment
	 *            the new status comment
	 */
	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	/**
	 * Gets the sync status.
	 *
	 * @return the sync status
	 */
	public String getSyncStatus() {
		return syncStatus;
	}

	/**
	 * Sets the sync status.
	 *
	 * @param syncStatus
	 *            the new sync status
	 */
	public void setSyncStatus(String syncStatus) {
		this.syncStatus = syncStatus;
	}

	/**
	 * Gets the sync type.
	 *
	 * @return the sync type
	 */
	public String getSyncType() {
		return syncType;
	}

	/**
	 * Sets the sync type.
	 *
	 * @param syncType
	 *            the new sync type
	 */
	public void setSyncType(String syncType) {
		this.syncType = syncType;
	}

}
