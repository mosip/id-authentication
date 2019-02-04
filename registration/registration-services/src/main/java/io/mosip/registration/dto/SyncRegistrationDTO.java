package io.mosip.registration.dto;

public class SyncRegistrationDTO {
	private String langCode;
	private String parentRegistrationId;
	private String registrationId;
	private String statusComment;
	private String syncStatus;
	private String syncType;

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public String getParentRegistrationId() {
		return parentRegistrationId;
	}

	public void setParentRegistrationId(String parentRegistrationId) {
		this.parentRegistrationId = parentRegistrationId;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public String getStatusComment() {
		return statusComment;
	}

	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	public String getSyncStatus() {
		return syncStatus;
	}

	public void setSyncStatus(String syncStatus) {
		this.syncStatus = syncStatus;
	}

	public String getSyncType() {
		return syncType;
	}

	public void setSyncType(String syncType) {
		this.syncType = syncType;
	}

}
