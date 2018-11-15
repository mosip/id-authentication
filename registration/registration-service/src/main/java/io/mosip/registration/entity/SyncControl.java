package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Entity class for SyncControl.
 * @author Mahesh Kumar
 *
 */
@Entity
@Table(schema="REG", name="SYNC_CONTROL")
public class SyncControl extends RegistrationCommonFields {

	@Id
	@Column(name="ID")
	private String id;
	@Column(name="SYNCJOB_ID")
	private String syncJobId;
	@Column(name="MACHINE_ID")
	private String machineId;
	@Column(name="REGCNTR_ID")
	private String regcntrId;
	@Column(name="LAST_SYNC_DTIMES")
	private Timestamp lastSyncDtimes;
	@Column(name="SYNCTRN_ID")
	private String synctrnId;
	@Column(name="LANG_CODE")
	private String langCode;
	@Column(name="IS_DELETED")
	private Boolean isDeleted;
	@Column(name="DEL_DTIMES")
	private Timestamp delDtime;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the syncJobId
	 */
	public String getSyncJobId() {
		return syncJobId;
	}
	/**
	 * @param syncJobId the syncJobId to set
	 */
	public void setSyncJobId(String syncJobId) {
		this.syncJobId = syncJobId;
	}
	/**
	 * @return the machineId
	 */
	public String getMachineId() {
		return machineId;
	}
	/**
	 * @param machineId the machineId to set
	 */
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}
	/**
	 * @return the regcntrId
	 */
	public String getRegcntrId() {
		return regcntrId;
	}
	/**
	 * @param regcntrId the regcntrId to set
	 */
	public void setRegcntrId(String regcntrId) {
		this.regcntrId = regcntrId;
	}
	/**
	 * @return the lastSyncDtimes
	 */
	public Timestamp getLastSyncDtimes() {
		return lastSyncDtimes;
	}
	/**
	 * @param lastSyncDtimes the lastSyncDtimes to set
	 */
	public void setLastSyncDtimes(Timestamp lastSyncDtimes) {
		this.lastSyncDtimes = lastSyncDtimes;
	}
	/**
	 * @return the synctrnId
	 */
	public String getSynctrnId() {
		return synctrnId;
	}
	/**
	 * @param synctrnId the synctrnId to set
	 */
	public void setSynctrnId(String synctrnId) {
		this.synctrnId = synctrnId;
	}
	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}
	/**
	 * @param langCode the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	/**
	 * @return the delDtime
	 */
	public Timestamp getDelDtime() {
		return delDtime;
	}
	/**
	 * @param delDtime the delDtime to set
	 */
	public void setDelDtime(Timestamp delDtime) {
		this.delDtime = delDtime;
	}
}
