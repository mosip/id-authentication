package io.mosip.authentication.core.util.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class AuditRequestDto.
 *
 * @author Manoj SP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditRequestDto {

	/** The event id. */
	@NotNull
	@Size(min = 1, max = 255)
	private String eventId;

	/** The event name. */
	@NotNull
	@Size(min = 1, max = 255)
	private String eventName;

	/** The event type. */
	@NotNull
	@Size(min = 1, max = 255)
	private String eventType;

	/** The action time stamp. */
	@NotNull
	private LocalDateTime actionTimeStamp;

	/** The host name. */
	@NotNull
	@Size(min = 1, max = 255)
	private String hostName;

	/** The host ip. */
	@NotNull
	@Size(min = 1, max = 255)
	private String hostIp;

	/** The application id. */
	@NotNull
	@Size(min = 1, max = 255)
	private String applicationId;

	/** The application name. */
	@NotNull
	@Size(min = 1, max = 255)
	private String applicationName;

	/** The session user id. */
	@NotNull
	@Size(min = 1, max = 255)
	private String sessionUserId;

	/** The session user name. */
	@NotNull
	@Size(min = 1, max = 255)
	private String sessionUserName;

	/** The id. */
	@NotNull
	@Size(min = 1, max = 255)
	private String id;

	/** The id type. */
	@NotNull
	@Size(min = 1, max = 255)
	private String idType;

	/** The created by. */
	@NotNull
	@Size(min = 1, max = 255)
	private String createdBy;

	/** The module name. */
	@Size(max = 255)
	private String moduleName;

	/** The module id. */
	@Size(max = 255)
	private String moduleId;

	/** The description. */
	@Size(max = 2048)
	private String description;

}
