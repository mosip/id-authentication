package io.mosip.registration.processor.rest.client.audit.dto;
	
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Audit Request class with {@link #actor}, {@link #action},
 * {@link #origin}, {@link #device}, {@link #description} fields to be captured
 * and recorded.
 *
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 */

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data

/**
 * Instantiates a new audit request dto.
 */
@NoArgsConstructor

/**
 * Instantiates a new audit request dto.
 *
 * @param eventId the event id
 * @param eventName the event name
 * @param eventType the event type
 * @param actionTimeStamp the action time stamp
 * @param hostName the host name
 * @param hostIp the host ip
 * @param applicationId the application id
 * @param applicationName the application name
 * @param sessionUserId the session user id
 * @param sessionUserName the session user name
 * @param id the id
 * @param idType the id type
 * @param createdBy the created by
 * @param moduleName the module name
 * @param moduleId the module id
 * @param description the description
 */
@AllArgsConstructor
public class AuditRequestDto {

	/** The event id. */
	@NotNull
	@Size(min = 1, max = 64)
	private String eventId;

	/** The event name. */
	@NotNull
	@Size(min = 1, max = 128)
	private String eventName;

	/** The event type. */
	@NotNull
	@Size(min = 1, max = 64)
	private String eventType;

	/** The action time stamp. */
	@NotNull
	private String actionTimeStamp;

	/** The host name. */
	@NotNull
	@Size(min = 1, max = 32)
	private String hostName;

	/** The host ip. */
	@NotNull
	@Size(min = 1, max = 16)
	private String hostIp;

	/** The application id. */
	@NotNull
	@Size(min = 1, max = 64)
	private String applicationId;

	/** The application name. */
	@NotNull
	@Size(min = 1, max = 128)
	private String applicationName;

	/** The session user id. */
	@NotNull
	@Size(min = 1, max = 64)
	private String sessionUserId;

	/** The session user name. */
	@Size(min = 1, max = 128)
	private String sessionUserName;

	/** The id. */
	@NotNull
	@Size(min = 1, max = 64)

	private String id;
	
	/** The id type. */
	@NotNull
	@Size(min = 1, max = 64)
	private String idType;

	/** The created by. */
	@NotNull
	@Size(min = 1, max = 255)
	private String createdBy;

	/** The module name. */
	@Size(max = 128)
	private String moduleName;

	/** The module id. */
	@Size(max = 64)
	private String moduleId;

	/** The description. */
	@Size(max = 2048)
	private String description;

}
