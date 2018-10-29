package io.mosip.registration.entity;

import java.time.OffsetDateTime;

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
	private String sJobId;
	@Column(name="MACHM_ID", length=64, nullable=true, updatable=true)
	private String machnId;
	@Column(name="CNTR_ID", length=32, nullable=true, updatable=true)
	private String cntrId;
	@Column(name="LAST_SYNC_DTIMEZ", nullable=false, updatable=false)
	private OffsetDateTime lastSyncDtimez;
	@Column(name="SYNCT_ID", length=32, nullable=false, updatable=false)
	private String synctId;
	@Column(name="LANG_CODE", length=3, nullable=true, updatable=true)
	private String langCode;
	@Column(name="IS_ACTIVE", nullable=false, updatable=true)
	@Type(type= "true_false")
	private boolean isActive;
	@Column(name="CR_BY", length=24, nullable=false, updatable=true)
	private String crBy;
	@Column(name="CR_DTIMESZ", nullable=false, updatable=true)
	private OffsetDateTime crDtime;
	@Column(name="UPD_BY", length=24, nullable=true, updatable=true)
	private String updBy;
	@Column(name="UPD_DTIMESZ", nullable=true, updatable=true)
	private OffsetDateTime updDtime;
	@Column(name="IS_DELETED", nullable=true, updatable=true)
	private boolean isDeleted;
	@Column(name="DEL_DTIMESZ", nullable=true, updatable=true)
	private OffsetDateTime delDtime;
	public String getSJobId() {
		return sJobId;
	}
	public void setSJobId(String sJobId) {
		this.sJobId = sJobId;
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
	public OffsetDateTime getLastSyncDtimez() {
		return lastSyncDtimez;
	}
	public void setLastSyncDtimez(OffsetDateTime lastSyncDtimez) {
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
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public String getCrBy() {
		return crBy;
	}
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}
	public OffsetDateTime getCrDtime() {
		return crDtime;
	}
	public void setCrDtime(OffsetDateTime crDtime) {
		this.crDtime = crDtime;
	}
	public String getUpdBy() {
		return updBy;
	}
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}
	public OffsetDateTime getUpdDtime() {
		return updDtime;
	}
	public void setUpdDtime(OffsetDateTime updDtime) {
		this.updDtime = updDtime;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public OffsetDateTime getDelDtime() {
		return delDtime;
	}
	public void setDelDtime(OffsetDateTime delDtime) {
		this.delDtime = delDtime;
	}
	
}
