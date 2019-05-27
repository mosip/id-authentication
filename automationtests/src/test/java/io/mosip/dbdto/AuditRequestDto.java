package io.mosip.dbdto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Audit Request class with {@link #actor}, {@link #action},
 * {@link #origin}, {@link #device}, {@link #description} fields to be captured
 * and recorded.
 *
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 */

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data

/**
 * Instantiates a new audit request dto.
 */
@NoArgsConstructor

/**
 * Instantiates a new audit request dto.
 *
 * @param eventId the event id
 * @param eventName the event name
 * @param eventType the event type
 * @param actionTimeStamp the action time stamp
 * @param hostName the host name
 * @param hostIp the host ip
 * @param applicationId the application id
 * @param applicationName the application name
 * @param sessionUserId the session user id
 * @param sessionUserName the session user name
 * @param id the id
 * @param idType the id type
 * @param createdBy the created by
 * @param moduleName the module name
 * @param moduleId the module id
 * @param description the description
 */
@AllArgsConstructor
public class AuditRequestDto {

	/** The event id. */
	@NotNull
	@Size(min = 1, max = 64)
	private String eventId;

	/** The event name. */
	@NotNull
	@Size(min = 1, max = 128)
	private String eventName;

	/** The event type. */
	@NotNull
	@Size(min = 1, max = 64)
	private String eventType;

	/** The action time stamp. */
	@NotNull
	private String actionTimeStamp;

	/** The host name. */
	@NotNull
	@Size(min = 1, max = 32)
	private String hostName;

	/** The host ip. */
	@NotNull
	@Size(min = 1, max = 16)
	private String hostIp;

	/** The application id. */
	@NotNull
	@Size(min = 1, max = 64)
	private String applicationId;

	/** The application name. */
	@NotNull
	@Size(min = 1, max = 128)
	private String applicationName;

	/** The session user id. */
	@NotNull
	@Size(min = 1, max = 64)
	private String sessionUserId;

	/** The session user name. */
	@Size(min = 1, max = 128)
	private String sessionUserName;

	/** The id. */
	@NotNull
	@Size(min = 1, max = 64)

	private String id;
	
	/** The id type. */
	@NotNull
	@Size(min = 1, max = 64)
	private String idType;

	/** The created by. */
	@NotNull
	@Size(min = 1, max = 255)
	private String createdBy;

	/** The module name. */
	@Size(max = 128)
	private String moduleName;

	/** The module id. */
	@Size(max = 64)
	private String moduleId;

	/** The description. */
	@Size(max = 2048)
	private String description;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getActionTimeStamp() {
		return actionTimeStamp;
	}

	public void setActionTimeStamp(String actionTimeStamp) {
		this.actionTimeStamp = actionTimeStamp;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getSessionUserId() {
		return sessionUserId;
	}

	public void setSessionUserId(String sessionUserId) {
		this.sessionUserId = sessionUserId;
	}

	public String getSessionUserName() {
		return sessionUserName;
	}

	public void setSessionUserName(String sessionUserName) {
		this.sessionUserName = sessionUserName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

}