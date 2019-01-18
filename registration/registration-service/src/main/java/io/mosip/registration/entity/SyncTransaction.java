package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
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
public class SyncTransaction  {
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private String id;
	@Column(name = "syncjob_id")
	private String syncJobId;
	@Column(name = "sync_dtimes")
	private Timestamp syncDateTime;
	@Column(name = "status_code")
	private String statusCode;
	@Column(name = "status_comment")
	private String statusComment;
	@Column(name = "trigger_point")
	private String triggerPoint;
	@Column(name = "sync_from")
	private String syncFrom;
	@Column(name = "sync_to")
	private String syncTo;
	@Column(name = "machine_id")
	private String machmId;
	@Column(name = "regcntr_id")
	private String cntrId;
	@Column(name = "ref_id")
	private String refId;
	@Column(name = "ref_id_type")
	private String refIdType;
	@Column(name = "sync_param")
	private String syncParam;
	@Column(name = "lang_code")
	private String langCode;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedDateTime;
	
	@Column(name = "CR_BY")
	private String crBy;
	@Column(name = "CR_DTIMES")
	private Timestamp crDtime;
	@Column(name = "UPD_BY")
	private String updBy;
	@Column(name = "UPD_DTIMES")
	private Timestamp updDtimes;
	
	public String getCrBy() {
		return crBy;
	}
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}
	public Timestamp getCrDtime() {
		return crDtime;
	}
	public void setCrDtime(Timestamp crDtime) {
		this.crDtime = crDtime;
	}
	public String getUpdBy() {
		return updBy;
	}
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}
	public Timestamp getUpdDtimes() {
		return updDtimes;
	}
	public void setUpdDtimes(Timestamp updDtimes) {
		this.updDtimes = updDtimes;
	}
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
	
	public Boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Timestamp getDeletedDateTime() {
		return deletedDateTime;
	}
	public void setDeletedDateTime(Timestamp deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}
	
	
}
