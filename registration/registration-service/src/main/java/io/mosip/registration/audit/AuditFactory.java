package io.mosip.registration.audit;

import org.springframework.data.auditing.AuditingHandler;

import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;

public interface AuditFactory {

	/**
	 * Static method to audit the events across Registration Processor Module.
	 * <p>
	 * This method takes {@code AuditEventEnum}, {@link Components}, audit
	 * description, refId and refIdType as inputs values from Session Context object
	 * namely createdBy, sessionUserId and sessionUserName to build the
	 * {@link AuditRequest} object. This {@link AuditRequest} object will be passed
	 * to the {@link AuditingHandler} which will persist the audit event in
	 * database.
	 * 
	 * @param auditEventEnum
	 *            this {@code Enum} contains the event details namely eventId,
	 *            eventType and eventName
	 * @param appModuleEnum
	 *            this {@code Enum} contains the application module details namely
	 *            moduleId and moduleName
	 * @param auditDescription
	 *            the description of the audit event
	 * @param refId
	 *            the ref id of the audit event
	 * @param refIdType
	 *            the ref id type of the audit event
	 */
	void audit(AuditEvent auditEventEnum, Components appModuleEnum, String auditDescription, String refId,
			String refIdType);

}