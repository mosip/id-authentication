package org.mosip.kernel.auditmanager.model;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * The Audit Entity class with {@link #actor}, {@link #action}, {@link #origin},
 * {@link #device}, {@link #description} fields to be captured and recorded
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "AUDITEVENTS")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Audit extends BaseAudit {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = -7791835434727848970L;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "EVENT_ID", nullable = false, updatable = false, length = 255)
	private String eventId;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "EVENT_NAME", nullable = false, updatable = false, length = 255)
	private String eventName;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "EVENT_TYPE", nullable = false, updatable = false, length = 255)
	private String eventType;

	@NotNull
	@Column(name = "ACTION_TIMESTAMP", nullable = false, updatable = false)
	private OffsetDateTime actionTimeStamp;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "HOST_NAME", nullable = false, updatable = false, length = 255)
	private String hostName;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "HOST_IP", nullable = false, updatable = false, length = 255)
	private String hostIp;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "APPLICATION_ID", nullable = false, updatable = false, length = 255)
	private String applicationId;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "APPLICATION_NAME", nullable = false, updatable = false, length = 255)
	private String applicationName;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "SESSION_USER_ID", nullable = false, updatable = false, length = 255)
	private String sessionUserId;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "SESSION_USER_NAME", nullable = false, updatable = false, length = 255)
	private String sessionUserName;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "ID", nullable = false, updatable = false, length = 255)
	private String id;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "ID_TYPE", nullable = false, updatable = false, length = 255)
	private String idType;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "CREATED_BY", nullable = false, updatable = false, length = 255)
	private String createdBy;

	@Size(max = 255)
	@Column(name = "MODULE_NAME", updatable = false, length = 255)
	private String moduleName;

	@Size(max = 255)
	@Column(name = "MODULE_ID", updatable = false, length = 255)
	private String moduleId;

	@Size(max = 2048)
	@Column(name = "DESCRIPTION", updatable = false, length = 2048)
	private String description;
}
