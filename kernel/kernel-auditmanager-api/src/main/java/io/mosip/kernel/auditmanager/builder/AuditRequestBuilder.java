package io.mosip.kernel.auditmanager.builder;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import io.mosip.kernel.auditmanager.request.AuditRequestDto;

/**
 * The Audit request builder class is used to create new {@link AuditRequestDto}
 * with all required fields
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
public class AuditRequestBuilder {

	/**
	 * The {@link AuditRequestDto} instance
	 */
	private AuditRequestDto auditRequest = null;

	/**
	 * Instantiate a new {@link AuditRequestDto}
	 */
	public AuditRequestBuilder() {
		auditRequest = new AuditRequestDto();
	}

	/**
	 * Add actionTimeStamp to this {@link AuditRequestBuilder}
	 * 
	 * @param actionTimeStamp
	 *            actionTimeStamp
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setActionTimeStamp(LocalDateTime actionTimeStamp) {
		auditRequest.setActionTimeStamp(actionTimeStamp);
		return this;
	}

	/**
	 * Add applicationId to this {@link AuditRequestBuilder}
	 * 
	 * @param applicationId
	 *            applicationId
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setApplicationId(String applicationId) {
		auditRequest.setApplicationId(applicationId);
		return this;
	}

	/**
	 * Add applicationName to this {@link AuditRequestBuilder}
	 * 
	 * @param applicationName
	 *            applicationName
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setApplicationName(String applicationName) {
		auditRequest.setApplicationName(applicationName);
		return this;
	}

	/**
	 * Add createdBy to this {@link AuditRequestBuilder}
	 * 
	 * @param createdBy
	 *            createdBy
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setCreatedBy(String createdBy) {
		auditRequest.setCreatedBy(createdBy);
		return this;
	}

	/**
	 * Add description to this {@link AuditRequestBuilder}
	 * 
	 * @param description
	 *            description
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setDescription(String description) {
		auditRequest.setDescription(description);
		return this;
	}

	/**
	 * Add eventId to this {@link AuditRequestBuilder}
	 * 
	 * @param eventId
	 *            eventId
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setEventId(String eventId) {
		auditRequest.setEventId(eventId);
		return this;
	}

	/**
	 * Add eventName to this {@link AuditRequestBuilder}
	 * 
	 * @param eventName
	 *            eventName
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setEventName(String eventName) {
		auditRequest.setEventName(eventName);
		return this;
	}

	/**
	 * Add eventType to this {@link AuditRequestBuilder}
	 * 
	 * @param eventType
	 *            eventType
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setEventType(String eventType) {
		auditRequest.setEventType(eventType);
		return this;
	}

	/**
	 * Add hostIp to this {@link AuditRequestBuilder}
	 * 
	 * @param hostIp
	 *            hostIp
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setHostIp(String hostIp) {
		auditRequest.setHostIp(hostIp);
		return this;
	}

	/**
	 * Add hostName to this {@link AuditRequestBuilder}
	 * 
	 * @param hostName
	 *            hostName
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setHostName(String hostName) {
		auditRequest.setHostName(hostName);
		return this;
	}

	/**
	 * Add id to this {@link AuditRequestBuilder}
	 * 
	 * @param id
	 *            id
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setId(String id) {
		auditRequest.setId(id);
		return this;
	}

	/**
	 * Add idType to this {@link AuditRequestBuilder}
	 * 
	 * @param idType
	 *            idType
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setIdType(String idType) {
		auditRequest.setIdType(idType);
		return this;
	}

	/**
	 * Add moduleId to this {@link AuditRequestBuilder}
	 * 
	 * @param moduleId
	 *            moduleId
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setModuleId(String moduleId) {
		auditRequest.setModuleId(moduleId);
		return this;
	}

	/**
	 * Add moduleName to this {@link AuditRequestBuilder}
	 * 
	 * @param moduleName
	 *            moduleName
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setModuleName(String moduleName) {
		auditRequest.setModuleName(moduleName);
		return this;
	}

	/**
	 * Add sessionUserId to this {@link AuditRequestBuilder}
	 * 
	 * @param sessionUserId
	 *            sessionUserId
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setSessionUserId(String sessionUserId) {
		auditRequest.setSessionUserId(sessionUserId);
		return this;
	}

	/**
	 * Add sessionUserName to this {@link AuditRequestBuilder}
	 * 
	 * @param sessionUserName
	 *            sessionUserName
	 * 
	 * @return {@link AuditRequestBuilder}
	 */
	public AuditRequestBuilder setSessionUserName(String sessionUserName) {
		auditRequest.setSessionUserName(sessionUserName);
		return this;
	}

	/**
	 * Builds {@link AuditRequestDto} from {@link AuditRequestBuilder}
	 * 
	 * @return The {@link AuditRequestDto}
	 */
	public AuditRequestDto build() {
		return auditRequest;
	}
}
