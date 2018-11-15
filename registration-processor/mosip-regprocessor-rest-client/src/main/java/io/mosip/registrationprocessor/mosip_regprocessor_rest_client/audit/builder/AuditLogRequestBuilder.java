package io.mosip.registrationprocessor.mosip_regprocessor_rest_client.audit.builder;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.ServerUtil;

/**
 * The Class AuditRequestBuilder.
 * 
 * @author Rishabh Keshari
 */
@Component
public class AuditLogRequestBuilder {

	
	@Autowired
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;
	
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
		auditRequestDto.setActionTimeStamp(LocalDateTime.now());
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
		
		
		registrationProcessorRestService.postApi(ApiName.AUDIT, "", "", auditRequestDto, void.class);
	}

}