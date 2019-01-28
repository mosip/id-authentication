package io.mosip.preregistration.core.common.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;


/**
 * This DTO class is use to Audit Logging.
 * @author Sanober Noor
 *Since 1.0.0
 */

@ToString
@Data
public class AuditLoggingDTO implements Serializable{

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1647047266757146239L;

	/**
	 * 
	 */
	String eventId;
	
	String eventName;
	String eventType;
	String actionTimeStamp;
	String hostName;
	String hostIp;
	String applicationId;
	String applicationName;
	String sessionUserId;
	String sessionUserName;
	String id;
	String idType;
	String createdBy;
	String moduleName;
	String moduleId;
	String description;
}
