package io.mosip.authentication.common.service.factory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.dto.AuditRequestDto;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import lombok.NoArgsConstructor;

/**
 * A factory for creating and building AuditRequest objects from
 * properties
 *
 * @author Manoj SP
 */
@Component
@NoArgsConstructor
public class AuditRequestFactory {

	/** The mosipLogger. */
    private static Logger mosipLogger = IdaLogger.getLogger(AuditRequestFactory.class);

    /**
     * Builds the request.
     *
     * @param module the module
     * @param event the event
     * @param id the id
     * @param idType the id type enum
     * @param desc the desc
     * @return the audit request dto
     */
    public RequestWrapper<AuditRequestDto> buildRequest(AuditModules module, AuditEvents event, String id, IdType idType, String desc) {
    	return buildRequest(module, event, id, idType.name(), desc);
    }

    /**
     * Builds the request.
     *
     * @param module the module
     * @param event the event
     * @param id the id
     * @param idType the id type name
     * @param desc the desc
     * @return the audit request dto
     */
    public RequestWrapper<AuditRequestDto> buildRequest(AuditModules module, AuditEvents event, String id, String idType, String desc) {
	AuditRequestDto request = new AuditRequestDto();
	String hostName;
	String hostAddress;

	try {
	    InetAddress inetAddress = InetAddress.getLocalHost();
	    hostName = inetAddress.getHostName();
	    hostAddress = inetAddress.getHostAddress();
	} catch (UnknownHostException ex) {
	    mosipLogger.error("sessionId", "AuditRequestFactory", ex.getClass().getName(), "Exception : " + ex);
	    hostName = 
	    hostAddress = EnvUtil.getAuditDefaultHostName();
	}

	request.setEventId(event.getEventId());
	request.setEventName(event.getEventName());
	request.setEventType(event.getEventType());
	request.setActionTimeStamp(DateUtils.getUTCCurrentDateTime());
	request.setHostName(hostName);
	request.setHostIp(hostAddress);
	request.setApplicationId(EnvUtil.getAppId());
	request.setApplicationName(EnvUtil.getAppName());
	request.setSessionUserId("sessionUserId");
	request.setSessionUserName("sessionUserName");
	request.setIdType(idType);
	request.setCreatedBy(EnvUtil.getUsername());
	request.setModuleName(module.getModuleName());
	request.setModuleId(module.getModuleId());
	request.setDescription(desc);
	request.setId(id);
	return RestRequestFactory.createRequest(request);
    }
}
