package io.mosip.registration.entity;

import java.sql.Timestamp;

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
public class AuditLogControl {

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
	@Column(name = "CR_BY")
	private String crBy;
	@Column(name = "CR_DTIMES")
	private Timestamp crDtime;
	@Column(name = "UPD_BY")
	private String updBy;
	@Column(name = "UPD_DTIMES")
	private Timestamp updDtimes;

}
