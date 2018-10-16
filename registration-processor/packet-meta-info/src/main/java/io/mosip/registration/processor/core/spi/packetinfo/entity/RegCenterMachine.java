package io.mosip.registration.processor.core.spi.packetinfo.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the reg_center_machine database table.
 * 
 */
@Entity
@Table(name="reg_center_machine")
@NamedQuery(name="RegCenterMachine.findAll", query="SELECT r FROM RegCenterMachine r")
public class RegCenterMachine implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="reg_id")
	private String regId;

	@Column(name="cntr_id")
	private String cntrId;

	@Column(name="cr_by")
	private String crBy;

	@Column(name="cr_dtimesz")
	private Timestamp crDtimesz;

	@Column(name="del_dtimesz")
	private Timestamp delDtimesz;

	@Column(name="is_active")
	private Boolean isActive;

	@Column(name="is_deleted")
	private Boolean isDeleted;

	private String latitude;

	private String longitude;

	@Column(name="machine_id")
	private String machineId;

	@Column(name="prereg_id")
	private String preregId;

	@Column(name="upd_by")
	private String updBy;

	@Column(name="upd_dtimesz")
	private Timestamp updDtimesz;

	public RegCenterMachine() {
		super();
	}

	public String getRegId() {
		return this.regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getCntrId() {
		return this.cntrId;
	}

	public void setCntrId(String cntrId) {
		this.cntrId = cntrId;
	}

	public String getCrBy() {
		return this.crBy;
	}

	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	public Timestamp getCrDtimesz() {
		return this.crDtimesz;
	}

	public void setCrDtimesz(Timestamp crDtimesz) {
		this.crDtimesz = crDtimesz;
	}

	public Timestamp getDelDtimesz() {
		return this.delDtimesz;
	}

	public void setDelDtimesz(Timestamp delDtimesz) {
		this.delDtimesz = delDtimesz;
	}

	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getMachineId() {
		return this.machineId;
	}

	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	public String getPreregId() {
		return this.preregId;
	}

	public void setPreregId(String preregId) {
		this.preregId = preregId;
	}

	public String getUpdBy() {
		return this.updBy;
	}

	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	public Timestamp getUpdDtimesz() {
		return this.updDtimesz;
	}

	public void setUpdDtimesz(Timestamp updDtimesz) {
		this.updDtimesz = updDtimesz;
	}

}