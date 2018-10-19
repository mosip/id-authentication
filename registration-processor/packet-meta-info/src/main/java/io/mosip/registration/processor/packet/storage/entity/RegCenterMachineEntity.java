package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The persistent class for the reg_center_machine database table.
 * 
 * @author Horteppa M1048399
 * @author Girish Yarru
 */
@Entity
@Table(name = "reg_center_machine", schema = "regprc")
public class RegCenterMachineEntity extends BasePacketEntity<RegCenterMachinePKEntity> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "prereg_id", nullable = false)
	private String preregId;

	@Column(name = "machine_id", nullable = false)
	private String machineId;

	@Column(name = "cntr_id", nullable = false)
	private String cntrId;

	private String latitude;

	private String longitude;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@Column(name = "cr_by")
	private String crBy = "MOSIP_SYSTEM";

	@Column(name = "cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	@Column(name = "upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updDtimesz;

	@Column(name = "del_dtimesz")
	@UpdateTimestamp
	private LocalDateTime delDtimesz;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	public String getPreregId() {
		return preregId;
	}

	public void setPreregId(String preregId) {
		this.preregId = preregId;
	}

	public String getMachineId() {
		return machineId;
	}

	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	public String getCntrId() {
		return cntrId;
	}

	public void setCntrId(String cntrId) {
		this.cntrId = cntrId;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getCrBy() {
		return crBy;
	}

	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	public LocalDateTime getCrDtimesz() {
		return crDtimesz;
	}

	public void setCrDtimesz(LocalDateTime crDtimesz) {
		this.crDtimesz = crDtimesz;
	}

	public String getUpdBy() {
		return updBy;
	}

	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	public LocalDateTime getUpdDtimesz() {
		return updDtimesz;
	}

	public void setUpdDtimesz(LocalDateTime updDtimesz) {
		this.updDtimesz = updDtimesz;
	}

	public LocalDateTime getDelDtimesz() {
		return delDtimesz;
	}

	public void setDelDtimesz(LocalDateTime delDtimesz) {
		this.delDtimesz = delDtimesz;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public RegCenterMachineEntity() {
		super();
	}

}