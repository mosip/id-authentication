package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class is to capture the time duration for each event
 * 
 * @author Sowmya
 * 
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditDTO extends BaseDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 6615819066351989845L;
	protected String eventId;
	protected String eventName;
	protected String eventType;
	protected LocalDateTime actionTimeStamp;
	protected String hostName;
	protected String hostIp;
	protected String applicationId;
	protected String applicationName;
	protected String sessionUserId;
	protected String sessionUserName;
	protected String id;
	protected String idType;
	protected String createdBy;
	protected String moduleName;
	protected String moduleId;
	protected String description;
	protected String uuid;
	protected LocalDateTime createdAt;

}
