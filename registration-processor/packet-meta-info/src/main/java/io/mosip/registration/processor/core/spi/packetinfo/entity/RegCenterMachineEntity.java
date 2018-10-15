package io.mosip.registration.processor.core.spi.packetinfo.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


/**
 * The persistent class for the reg_center_machine database table.
 * 
 * @author Horteppa M1048399
 */
@Entity
@Table(name="reg_center_machine", schema = "regprc")
@NamedQuery(name="RegCenterMachineEntity.findAll", query="SELECT r FROM RegCenterMachineEntity r")
public class RegCenterMachineEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="reg_id")
	private String regId;

	@Column(name="cntr_id")
	private String cntrId;

	@Column(name="cr_by")
	private String crBy;

	@Column(name="cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	@Column(name="del_dtimesz")
	@UpdateTimestamp
	private LocalDateTime delDtimesz;

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
	@UpdateTimestamp
	private LocalDateTime updDtimesz;

	public RegCenterMachineEntity() {
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

	public LocalDateTime getCrDtimesz() {
		return this.crDtimesz;
	}

	public void setCrDtimesz(LocalDateTime crDtimesz) {
		this.crDtimesz = crDtimesz;
	}

	public LocalDateTime getDelDtimesz() {
		return this.delDtimesz;
	}

	public void setDelDtimesz(LocalDateTime delDtimesz) {
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

	public LocalDateTime getUpdDtimesz() {
		return this.updDtimesz;
	}

	public void setUpdDtimesz(LocalDateTime updDtimesz) {
		this.updDtimesz = updDtimesz;
	}

}