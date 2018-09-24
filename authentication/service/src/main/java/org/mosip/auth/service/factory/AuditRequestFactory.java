package org.mosip.auth.service.factory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.OffsetDateTime;

import org.mosip.auth.core.util.dto.AuditRequestDto;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * A factory for creating and building AuditRequest objects from
 * audit.properties
 *
 * @author Manoj SP
 */
@Component
public class AuditRequestFactory {
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
	public AuditRequestDto buildRequest(String moduleId, String description) {
		AuditRequestDto request = new AuditRequestDto();
		String hostName;
		String hostAddress;

		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			hostName = inetAddress.getHostName();
			hostAddress = inetAddress.getHostAddress();
		} catch (UnknownHostException ex) {
			logger.error("sessionId", "AuditRequestFactory", ex.getClass().getName(), ex.toString());
			hostName = env.getProperty("audit.defaultHostName");
			hostAddress = env.getProperty("audit.defaultHostAddress");
		}

		request.setEventId("eventId");
		request.setEventName("eventName");
		request.setEventType("eventType");
		request.setActionTimeStamp(OffsetDateTime.now());
		request.setHostName(hostName);
		request.setHostIp(hostAddress);
		request.setApplicationId(env.getProperty("application.id"));
		request.setApplicationName(env.getProperty("application.name"));
		request.setSessionUserId("sessionUserId");
		request.setSessionUserName("sessionUserName");
		request.setId("id");
		request.setIdType("idType");
		request.setCreatedBy("createdBy"); //TODO get from system
		request.setModuleName("moduleName"); //TODO get from constant
		request.setModuleId(moduleId);
		request.setDescription(description);

		return request;
	}
}
