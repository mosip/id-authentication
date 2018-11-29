package io.mosip.registration.audit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.auditmanager.spi.AuditHandler;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;

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

	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;
	@Autowired
	private Environment environment;

	/* (non-Javadoc)
	 * @see io.mosip.registration.audit.AuditFactory#audit(io.mosip.registration.constants.AuditEvent, io.mosip.registration.constants.Components, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void audit(AuditEvent auditEventEnum, Components appModuleEnum, String auditDescription, String refId,
			String refIdType) {

		// Get UserContext Object from SessionContext
		UserContext userContext = SessionContext.getInstance().getUserContext();
		String userId = userContext.getUserId() == null ? "NA" : userContext.getUserId();
		String userName = userContext.getName() == null ? "NA" : userContext.getName(); 

		// Getting Host IP Address and Name
		String hostIP = null;
		String hostName = null;
		try {
			InetAddress hostInetAddress = InetAddress.getLocalHost();
			hostIP = hostInetAddress.getHostAddress();
			hostName = hostInetAddress.getHostName();
		} catch (UnknownHostException unknownHostException) {
			hostIP = environment.getProperty(RegistrationConstants.HOST_IP);
			hostName = environment.getProperty(RegistrationConstants.HOST_NAME);
		}

		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
		auditRequestBuilder.setActionTimeStamp(LocalDateTime.now())
				.setApplicationId(environment.getProperty(RegistrationConstants.AUDIT_APPLICATION_ID))
				.setApplicationName(environment.getProperty(RegistrationConstants.AUDIT_APPLICATION_NAME))
				.setCreatedBy(userName).setDescription(auditDescription).setEventId(auditEventEnum.getId())
				.setEventName(auditEventEnum.getName()).setEventType(auditEventEnum.getType()).setHostIp(hostIP)
				.setHostName(hostName).setId(refId).setIdType(refIdType).setModuleId(appModuleEnum.getId())
				.setModuleName(appModuleEnum.getName()).setSessionUserId(userId)
				.setSessionUserName(userName);
		auditHandler.addAudit(auditRequestBuilder.build());
	}
}
