package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * Entity class for sync_job_def
 * 
 * @author Dinesh Ashokan
 * @since 1.0.0
 *
 */
@Entity
@Table(schema = "reg", name = "sync_job_def")
public class SyncJobDef extends RegistrationCommonFields {
	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "name", length = 64, nullable = false)
	private String name;
	@Column(name = "api_name", length = 64)
	private String apiName;
	@Column(name = "parent_syncjob_id", length = 36)
	private String parentSyncJobId;
	@Column(name = "sync_freq", length = 36)
	private String syncFrequency;
	@Column(name = "lock_duration", length = 36)
	private String lockDuration;
	@Column(name = "lang_code", length = 3)
	private String langCode;

	@Column(name = "is_deleted", nullable = false, updatable = true)
	@Type(type = "true_false")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedDateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	

	public String getParentSyncJobId() {
		return parentSyncJobId;
	}

	public void setParentSyncJobId(String parentSyncJobId) {
		this.parentSyncJobId = parentSyncJobId;
	}

	public String getSyncFrequency() {
		return syncFrequency;
	}

	public void setSyncFrequency(String syncFrequency) {
		this.syncFrequency = syncFrequency;
	}

	public String getLockDuration() {
		return lockDuration;
	}

	public void setLockDuration(String lockDuration) {
		this.lockDuration = lockDuration;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Timestamp getDeletedDateTime() {
		return deletedDateTime;
	}

	public void setDeletedDateTime(Timestamp deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}

}
