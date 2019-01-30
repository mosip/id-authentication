package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity for maintaining the audit logs sent along with the registration packet
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Entity
@Table(schema = "reg", name = "audit_log_control")
@Getter
@Setter
@AttributeOverride(column = @Column(name = "isActive", insertable = false, updatable = false), name = "isActive")
public class AuditLogControl extends RegistrationCommonFields {

	@Id
	@Column(name = "reg_id")
	private String registrationId;
	@Column(name = "audit_log_from_dtimes")
	private Timestamp auditLogFromDateTime;
	@Column(name = "audit_log_to_dtimes")
	private Timestamp auditLogToDateTime;
	@Column(name = "audit_log_sync_dtimes")
	private Timestamp auditLogSyncDateTime;
	@Column(name = "audit_log_purge_dtimes")
	private Timestamp auditLogPurgeDateTime;

}
