package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity class for sync_control
 * @author Dinesh Ashokan
 * @since 1.0.0
 *
 */
@Entity
@Table(schema="reg",name="sync_transaction")
public class SyncTransaction  extends RegistrationCommonFields{
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", length = 36)
	private String id;
	@Column(name = "syncjob_id", length = 36, nullable = false)
	private String syncJobId;
	@Column(name = "sync_dtimes", nullable = false)
	private Timestamp syncDateTime;
	@Column(name = "status_code", length = 36, nullable = false)
	private String statusCode;
	@Column(name = "status_comment", length = 256)
	private String statusComment;
	@Column(name = "trigger_point", length = 32, nullable = false)
	private String triggerPoint;
	@Column(name = "sync_from", length = 32)
	private String syncFrom;
	@Column(name = "sync_to", length = 32)
	private String syncTo;
	@Column(name = "machine_id", length = 36)
	private String machmId;
	@Column(name = "regcntr_id", length = 36)
	private String cntrId;
	@Column(name = "ref_id", length = 36)
	private String refId;
	@Column(name = "ref_id_type", length = 64)
	private String refIdType;
	@Column(name = "sync_param", length = 2048)
	private String syncParam;
	@Column(name = "lang_code", length = 3)
	private String langCode;
	@Column(name = "is_active")
	private boolean isActive;
	@Column(name = "is_deleted")
	private boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedDateTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSyncJobId() {
		return syncJobId;
	}
	public void setSyncJobId(String syncJobId) {
		this.syncJobId = syncJobId;
	}
	public Timestamp getSyncDateTime() {
		return syncDateTime;
	}
	public void setSyncDateTime(Timestamp syncDateTime) {
		this.syncDateTime = syncDateTime;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusComment() {
		return statusComment;
	}
	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}
	public String getTriggerPoint() {
		return triggerPoint;
	}
	public void setTriggerPoint(String triggerPoint) {
		this.triggerPoint = triggerPoint;
	}
	public String getSyncFrom() {
		return syncFrom;
	}
	public void setSyncFrom(String syncFrom) {
		this.syncFrom = syncFrom;
	}
	public String getSyncTo() {
		return syncTo;
	}
	public void setSyncTo(String syncTo) {
		this.syncTo = syncTo;
	}
	public String getMachmId() {
		return machmId;
	}
	public void setMachmId(String machmId) {
		this.machmId = machmId;
	}
	public String getCntrId() {
		return cntrId;
	}
	public void setCntrId(String cntrId) {
		this.cntrId = cntrId;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getRefIdType() {
		return refIdType;
	}
	public void setRefIdType(String refIdType) {
		this.refIdType = refIdType;
	}
	public String getSyncParam() {
		return syncParam;
	}
	public void setSyncParam(String syncParam) {
		this.syncParam = syncParam;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Timestamp getDeletedDateTime() {
		return deletedDateTime;
	}
	public void setDeletedDateTime(Timestamp deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}
	
	
}
