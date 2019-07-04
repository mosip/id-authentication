package io.mosip.registration.dto;

import java.time.LocalDateTime;

/**
 * This class is to capture the time duration for each event
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class AuditDTO extends BaseDTO {
	protected String uuid;
	protected LocalDateTime createdAt;
	protected String eventId;
	protected String eventName;
	protected String eventType;
	protected LocalDateTime actionTimeStamp;
	protected String hostName;
	protected String hostIp;
	protected String applicationId;
	protected String applicationName;
	protected String sessionUserId;
	protected String sessionUserName;
	protected String id;
	protected String idType;
	protected String createdBy;
	protected String moduleName;
	protected String moduleId;
	protected String description;

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the createdAt
	 */
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt
	 *            the createdAt to set
	 */
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId
	 *            the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @param eventName
	 *            the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the actionTimeStamp
	 */
	public LocalDateTime getActionTimeStamp() {
		return actionTimeStamp;
	}

	/**
	 * @param actionTimeStamp
	 *            the actionTimeStamp to set
	 */
	public void setActionTimeStamp(LocalDateTime actionTimeStamp) {
		this.actionTimeStamp = actionTimeStamp;
	}

	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName
	 *            the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * @return the hostIp
	 */
	public String getHostIp() {
		return hostIp;
	}

	/**
	 * @param hostIp
	 *            the hostIp to set
	 */
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	/**
	 * @return the applicationId
	 */
	public String getApplicationId() {
		return applicationId;
	}

	/**
	 * @param applicationId
	 *            the applicationId to set
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * @param applicationName
	 *            the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * @return the sessionUserId
	 */
	public String getSessionUserId() {
		return sessionUserId;
	}

	/**
	 * @param sessionUserId
	 *            the sessionUserId to set
	 */
	public void setSessionUserId(String sessionUserId) {
		this.sessionUserId = sessionUserId;
	}

	/**
	 * @return the sessionUserName
	 */
	public String getSessionUserName() {
		return sessionUserName;
	}

	/**
	 * @param sessionUserName
	 *            the sessionUserName to set
	 */
	public void setSessionUserName(String sessionUserName) {
		this.sessionUserName = sessionUserName;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the idType
	 */
	public String getIdType() {
		return idType;
	}

	/**
	 * @param idType
	 *            the idType to set
	 */
	public void setIdType(String idType) {
		this.idType = idType;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the moduleName
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * @param moduleName
	 *            the moduleName to set
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * @return the moduleId
	 */
	public String getModuleId() {
		return moduleId;
	}

	/**
	 * @param moduleId
	 *            the moduleId to set
	 */
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}

