package io.mosip.registration.audit;

import org.springframework.data.auditing.AuditingHandler;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.dto.ResponseDTO;

/**
 * The wrapper interface to log the audits
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface AuditManagerService {

	/**
	 * Audits the events across Registration-Client Module.
	 * <p>
	 * This method takes {@link AuditEvent}, {@link Components}, audit
	 * description, refId and refIdType as inputs, other values from Session Context object
	 * namely createdBy, sessionUserId and sessionUserName to build the
	 * {@link AuditRequestBuilder} object. This {@link AuditRequestBuilder} object will be passed
	 * to the {@link AuditingHandler} which will persist the audit event in
	 * database.
	 * 
	 * @param auditEventEnum
	 *            this {@code Enum} contains the event details namely eventId,
	 *            eventType and eventName
	 * @param appModuleEnum
	 *            this {@code Enum} contains the application module details namely
	 *            moduleId and moduleName
	 * @param refId
	 *            the ref id of the audit event
	 * @param refIdType
	 *            the ref id type of the audit event
	 */
	void audit(AuditEvent auditEventEnum, Components appModuleEnum, String refId,
			String refIdType);
	
	/**
	 * Delete Audit Logs
	 * 
	 * @return response of deletion
	 */
	ResponseDTO deleteAuditLogs();

}