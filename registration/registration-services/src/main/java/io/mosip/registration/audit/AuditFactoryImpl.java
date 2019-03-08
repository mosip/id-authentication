package io.mosip.registration.audit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.auditmanager.spi.AuditHandler;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.context.SessionContext;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

/**
 * Class to Audit the events of Registration.
 * <p>
 * This class creates a wrapper around {@link AuditRequest} class. This class
 * creates a {@link AuditRequest} object for each audit event and persists the
 * same.
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class AuditFactoryImpl implements AuditFactory {

	private static final Logger LOGGER = AppConfig.getLogger(AuditFactoryImpl.class);
	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;
	@Value("${mosip.registration.audit_application_id:}")
	private String applicationId;
	@Value("${mosip.registration.audit_application_name:}")
	private String applicationName;
	@Value("${mosip.registration.audit_default_host_ip:}")
	private String defaultHostIP;
	@Value("${mosip.registration.audit_default_host_name:}")
	private String defaultHostName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.audit.AuditFactory#audit(io.mosip.registration.
	 * constants.AuditEvent, io.mosip.registration.constants.Components,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void audit(AuditEvent auditEventEnum, Components appModuleEnum, String refId, String refIdType) {

		// Getting Host IP Address and Name
		String hostIP = defaultHostIP;
		String hostName = defaultHostName;
		try {
			hostIP = InetAddress.getLocalHost().getHostAddress();
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException unknownHostException) {
			LOGGER.info("REGISTRATION-AUDIT_FACTORY-AUDIT", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(unknownHostException));
		}

		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
		auditRequestBuilder.setActionTimeStamp(LocalDateTime.now(ZoneOffset.UTC)).setApplicationId(applicationId)
				.setApplicationName(applicationName).setCreatedBy(SessionContext.userName())
				.setDescription(auditEventEnum.getDescription()).setEventId(auditEventEnum.getId())
				.setEventName(auditEventEnum.getName()).setEventType(auditEventEnum.getType()).setHostIp(hostIP)
				.setHostName(hostName).setId(refId).setIdType(refIdType).setModuleId(appModuleEnum.getId())
				.setModuleName(appModuleEnum.getName()).setSessionUserId(SessionContext.userId())
				.setSessionUserName(SessionContext.userName());
		auditHandler.addAudit(auditRequestBuilder.build());
	}
}
