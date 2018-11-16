package io.mosip.registration.processor.auditmanager.requestbuilder;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.auditmanager.client.AuditmanagerClient;
import io.mosip.registration.processor.auditmanager.dto.AuditRequestDto;
import io.mosip.registration.processor.core.constant.AuditLogConstant;
import io.mosip.registration.processor.core.util.ServerUtil;
/**
 * The Class AuditRequestBuilder.
 * 
 * @author Jyoti Prakash Nayak
 */
@Component
public class ClientAuditRequestBuilder {

	@Autowired
	AuditmanagerClient auditmanagerClient;

	/**
	 * Creates the audit request builder.
	 *
	 * @param description            the description
	 * @param eventId            the event id
	 * @param eventName            the event name
	 * @param eventType            the event type
	 * @param registrationId the registration id
	 * 
	 */
	public void createAuditRequestBuilder(String description, String eventId, String eventName, String eventType,
			String registrationId) {
		
		
		AuditRequestDto auditRequestDto= new AuditRequestDto();
		auditRequestDto.setDescription(description);
		auditRequestDto.setActionTimeStamp(OffsetDateTime.now());
		auditRequestDto.setApplicationId(AuditLogConstant.MOSIP_4.toString());
		auditRequestDto.setApplicationName(AuditLogConstant.REGISTRATION_PROCESSOR.toString());
		auditRequestDto.setCreatedBy(AuditLogConstant.SYSTEM.toString());
		auditRequestDto.setEventId(eventId);
		auditRequestDto.setEventName(eventName);
		auditRequestDto.setEventType(eventType);
		auditRequestDto.setHostIp(ServerUtil.getServerUtilInstance().getServerIp());
		auditRequestDto.setHostName(ServerUtil.getServerUtilInstance().getServerName());
		auditRequestDto.setId(registrationId);
		auditRequestDto.setIdType(AuditLogConstant.REGISTRATION_ID.toString());
		auditRequestDto.setModuleId(null);
		auditRequestDto.setModuleName(null);
		auditRequestDto.setSessionUserId(AuditLogConstant.SYSTEM.toString());
		auditRequestDto.setSessionUserName(null);
		
		auditmanagerClient.addAudit(auditRequestDto);
	}

}
