package io.mosip.authentication.core.util.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Audit Request class with {@link #actor}, {@link #action},
 * {@link #origin}, {@link #device}, {@link #description} fields to be captured
 * and recorded
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditRequestDto {

	@NotNull
	@Size(min = 1, max = 255)
	private String eventId;

	@NotNull
	@Size(min = 1, max = 255)
	private String eventName;

	@NotNull
	@Size(min = 1, max = 255)
	private String eventType;

	@NotNull
	private LocalDateTime actionTimeStamp;

	@NotNull
	@Size(min = 1, max = 255)
	private String hostName;

	@NotNull
	@Size(min = 1, max = 255)
	private String hostIp;

	@NotNull
	@Size(min = 1, max = 255)
	private String applicationId;

	@NotNull
	@Size(min = 1, max = 255)
	private String applicationName;

	@NotNull
	@Size(min = 1, max = 255)
	private String sessionUserId;

	@NotNull
	@Size(min = 1, max = 255)
	private String sessionUserName;

	@NotNull
	@Size(min = 1, max = 255)
	private String id;

	@NotNull
	@Size(min = 1, max = 255)
	private String idType;

	@NotNull
	@Size(min = 1, max = 255)
	private String createdBy;

	@Size(max = 255)
	private String moduleName;

	@Size(max = 255)
	private String moduleId;

	@Size(max = 2048)
	private String description;

}
