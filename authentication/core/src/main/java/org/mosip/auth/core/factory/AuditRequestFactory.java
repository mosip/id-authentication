package org.mosip.auth.core.factory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.OffsetDateTime;

import org.mosip.auth.core.constant.AuditServicesConstants;
import org.mosip.auth.core.util.dto.AuditRequestDto;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * A factory for creating and building AuditRequest objects from
 * audit.properties
 *
 * @author Manoj SP
 */
@Component
@PropertySource("classpath:audit.properties")
public class AuditRequestFactory {
	private static final String HOST_ADDRESS = "127.0.0.1";
	private static final String HOST_NAME = "localhost";
	private MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	@Autowired
	private Environment env;

	/**
	 * Builds the request.
	 *
	 * @return the audit request dto
	 */
	public AuditRequestDto buildRequest(AuditServicesConstants auditService) {
		AuditRequestDto request = new AuditRequestDto();
		String hostName;
		String hostAddress;
		String serviceName = auditService.getServiceName();

		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			hostName = inetAddress.getHostName();
			hostAddress = inetAddress.getHostAddress();
		} catch (UnknownHostException ex) {
			logger.error("sessionId", "AuditRequestFactory", ex.getClass().getName(), ex.toString());
			hostName = HOST_NAME;
			hostAddress = HOST_ADDRESS;
		}

		request.setEventId(env.getProperty(serviceName.concat(".audit.eventId")));
		request.setEventName(env.getProperty(serviceName.concat(".audit.eventName")));
		request.setEventType(env.getProperty(serviceName.concat(".audit.eventType")));
		request.setActionTimeStamp(OffsetDateTime.now());
		request.setHostName(hostName);
		request.setHostIp(hostAddress);
		request.setApplicationId(env.getProperty(serviceName.concat(".audit.applicationId")));
		request.setApplicationName(env.getProperty(serviceName.concat(".audit.applicationName")));
		request.setSessionUserId("sessionUserId");
		request.setSessionUserName("sessionUserName");
		request.setId(env.getProperty(serviceName.concat(".audit.id")));
		request.setIdType(env.getProperty(serviceName.concat(".audit.idType")));
		request.setCreatedBy("createdBy");
		request.setModuleName("moduleName");
		request.setModuleId("moduleId");
		request.setDescription("description");

		return request;
	}
}
