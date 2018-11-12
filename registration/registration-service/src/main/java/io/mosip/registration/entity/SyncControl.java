package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * The Entity class for SyncControl.
 * @author Mahesh Kumar
 *
 */
@Entity
@Table(schema="REG", name="SYNC_CONTROL")
public class SyncControl {

	@Id
	@Column(name="SJOB_ID", length=32, nullable=false, updatable=false)
	private String syncJobId;
	@Column(name="MACHM_ID", length=64, nullable=true, updatable=true)
	private String machnId;
	@Column(name="CNTR_ID", length=32, nullable=true, updatable=true)
	private String cntrId;
	@Column(name="LAST_SYNC_DTIMEZ", nullable=false, updatable=false)
	private Timestamp lastSyncDtimez;
	@Column(name="SYNCT_ID", length=32, nullable=false, updatable=false)
	private String synctId;
	@Column(name="LANG_CODE", length=3, nullable=true, updatable=true)
	private String langCode;
	@Column(name="IS_ACTIVE", nullable=false, updatable=true)
	@Type(type= "true_false")
	private Boolean isActive;
	@Column(name="CR_BY", length=24, nullable=false, updatable=true)
	private String crBy;
	@Column(name="CR_DTIMESZ", nullable=false, updatable=true)
	private Timestamp crDtime;
	@Column(name="UPD_BY", length=24, nullable=true, updatable=true)
	private String updBy;
	@Column(name="UPD_DTIMESZ", nullable=true, updatable=true)
	private Timestamp updDtime;
	@Column(name="IS_DELETED", nullable=true, updatable=true)
	private Boolean isDeleted;
	@Column(name="DEL_DTIMESZ", nullable=true, updatable=true)
	private Timestamp delDtime;
	public String getsyncJobId() {
		return syncJobId;
	}
	public void setsyncJobId(String syncJobId) {
		this.syncJobId = syncJobId;
	}
	public String getMachnId() {
		return machnId;
	}
	public void setMachnId(String machnId) {
		this.machnId = machnId;
	}
	public String getCntrId() {
		return cntrId;
	}
	public void setCntrId(String cntrId) {
		this.cntrId = cntrId;
	}
	public Timestamp getLastSyncDtimez() {
		return lastSyncDtimez;
	}
	public void setLastSyncDtimez(Timestamp lastSyncDtimez) {
		this.lastSyncDtimez = lastSyncDtimez;
	}
	public String getSynctId() {
		return synctId;
	}
	public void setSynctId(String synctId) {
		this.synctId = synctId;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	public Boolean isActive() {
		return isActive;
	}
	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}
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
	public Timestamp getUpdDtime() {
		return updDtime;
	}
	public void setUpdDtime(Timestamp updDtime) {
		this.updDtime = updDtime;
	}
	public Boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Timestamp getDelDtime() {
		return delDtime;
	}
	public void setDelDtime(Timestamp delDtime) {
		this.delDtime = delDtime;
	}
	
}
