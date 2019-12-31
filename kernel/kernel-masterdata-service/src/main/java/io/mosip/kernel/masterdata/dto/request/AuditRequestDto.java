package io.mosip.kernel.masterdata.dto.request;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditRequestDto {
	@NotNull
	@Size(min = 1, max = 64)
	private String eventId;

	@NotNull
	@Size(min = 1, max = 128)
	private String eventName;

	@NotNull
	@Size(min = 1, max = 64)
	private String eventType;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime actionTimeStamp;

	@NotNull
	@Size(min = 1, max = 128)
	private String hostName;

	@NotNull
	@Size(min = 1, max = 16)
	private String hostIp;

	@NotNull
	@Size(min = 1, max = 64)
	private String applicationId;

	@NotNull
	@Size(min = 1, max = 128)
	private String applicationName;

	@NotNull
	@Size(min = 1, max = 256)
	private String sessionUserId;

	@Size(min = 1, max = 128)
	private String sessionUserName;

	@NotNull
	@Size(min = 1, max = 64)

	private String id;
	@NotNull
	@Size(min = 1, max = 64)
	private String idType;

	@NotNull
	@Size(min = 1, max = 256)
	private String createdBy;

	@Size(max = 128)
	private String moduleName;

	@Size(max = 64)
	private String moduleId;

	@Size(max = 2048)
	private String description;

	private transient int eventIdCounter = 800;
}
