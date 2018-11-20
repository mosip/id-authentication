package io.mosip.registration.processor.rest.client.audit.builder;

import java.time.LocalDateTime;import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.ServerUtil;
import io.mosip.registration.processor.rest.client.audit.dto.AuditRequestDto;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;


/**
 * The Class AuditRequestBuilder.
 * 
 * @author Rishabh Keshari
 */
@Component
public class AuditLogRequestBuilder {

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(AuditLogRequestBuilder.class);

	/** The registration processor rest service. */
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
	 * @return the audit response dto
	 */
	public AuditResponseDto createAuditRequestBuilder(String description, String eventId, String eventName, String eventType,
			String registrationId) {

		AuditRequestDto auditRequestDto=null;
		AuditResponseDto auditResponseDto=null;

		try {
			
			auditRequestDto= new AuditRequestDto();
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
			auditResponseDto=(AuditResponseDto)registrationProcessorRestService.postApi(ApiName.AUDIT, "", "", auditRequestDto, AuditResponseDto.class);

		} catch (ApisResourceAccessException arae) {

			LOGGER.error(arae.getMessage());
			
		}

		return auditResponseDto;
	}

}