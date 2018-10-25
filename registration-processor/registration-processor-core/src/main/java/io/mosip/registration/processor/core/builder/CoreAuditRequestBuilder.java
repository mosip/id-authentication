package io.mosip.registration.processor.core.builder;

import java.net.UnknownHostException;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.util.ServerUtil;

/**
 * The Class AuditRequestBuilder.
 * 
 * @author Ranjitha
 */
@Component
public class CoreAuditRequestBuilder {

	/** The audit request builder. */
	@Autowired
	private AuditRequestBuilder auditRequestBuilder;

	/** The audit handler. */
	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

	/**
	 * AuditRequestBuilder Constructor.
	 */
	private CoreAuditRequestBuilder() {
		super();
	}

	/**
	 * Creates the audit request builder.
	 *
	 * @param description the description
	 * @param eventId the event id
	 * @param eventName the event name
	 * @param eventType the event type
	 * @param regisrationId the registration id
	 * @return true, if successful
	 * @throws UnknownHostException 
	 */
	public void createAuditRequestBuilder(String description, String eventId, String eventName, String eventType,
			String registrationId) throws UnknownHostException {
		auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
				.setApplicationId(AuditLogConstant.MOSIP_4.toString())
				.setApplicationName(AuditLogConstant.REGISTRATION_PROCESSOR.toString())
				.setCreatedBy(AuditLogConstant.SYSTEM.toString()).setDescription(description).setEventId(eventId)
				.setEventName(eventName).setEventType(eventType).setHostIp(ServerUtil.getServerUtilInstance().getServerIp()).setHostName(ServerUtil.getServerUtilInstance().getServerName()).setId(registrationId)
				.setIdType(AuditLogConstant.REGISTRATION_ID.toString()).setModuleId(null).setModuleName(null)
				.setSessionUserId(AuditLogConstant.SYSTEM.toString()).setSessionUserName(null);

		AuditRequestDto auditRequestDto = auditRequestBuilder.build();
		auditHandler.writeAudit(auditRequestDto);
	}

}
