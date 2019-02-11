package io.mosip.kernel.idrepo.factory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idrepo.constant.AuditEvents;
import io.mosip.kernel.core.idrepo.constant.AuditModules;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.idrepo.config.IdRepoLogger;
import io.mosip.kernel.idrepo.dto.AuditRequestDto;
import lombok.NoArgsConstructor;

/**
 * A factory for creating and building AuditRequest objects from
 * audit.properties
 *
 * @author Manoj SP
 */
@Component
@NoArgsConstructor
public class AuditRequestFactory {

    /** The mosipLogger. */
    private static Logger mosipLogger = IdRepoLogger.getLogger(AuditRequestFactory.class);

    /** The env. */
    @Autowired
    private Environment env;

    /**
     * Builds the request.
     *
     * @param module
     *            the module
     * @param event
     *            the event
     * @param id
     *            the id
     * @param idType
     *            the id type
     * @param desc
     *            the desc
     * @return the audit request dto
     */
    public AuditRequestDto buildRequest(AuditModules module, AuditEvents event, String id, String desc) {
	AuditRequestDto request = new AuditRequestDto();
	String hostName;
	String hostAddress;

	try {
	    InetAddress inetAddress = InetAddress.getLocalHost();
	    hostName = inetAddress.getHostName();
	    hostAddress = inetAddress.getHostAddress();
	} catch (UnknownHostException ex) {
	    mosipLogger.error("sessionId", "AuditRequestFactory", ex.getClass().getName(), "Exception : " + ex);
	    hostName = env.getProperty("audit.defaultHostName");
	    hostAddress = env.getProperty("audit.defaultHostAddress");
	}

	request.setEventId(event.getEventId());
	request.setEventName(event.getEventName());
	request.setEventType(event.getEventType());
	request.setActionTimeStamp(DateUtils.getUTCCurrentDateTime());
	request.setHostName(hostName);
	request.setHostIp(hostAddress);
	request.setApplicationId(env.getProperty("application.id"));
	request.setApplicationName(env.getProperty("application.name"));
	request.setSessionUserId("sessionUserId");
	request.setSessionUserName("sessionUserName");
	request.setId(id);
	request.setIdType("D");
	request.setCreatedBy(env.getProperty("user.name"));
	request.setModuleName(module.getModuleName());
	request.setModuleId(module.getModuleId());
	request.setDescription(desc);

	return request;
    }
}
