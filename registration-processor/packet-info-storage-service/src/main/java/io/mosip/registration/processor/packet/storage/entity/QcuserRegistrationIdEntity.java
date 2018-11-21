package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The persistent class for the qcuser_registration_id database table.
 * 
 */
@Entity
@Table(name = "qcuser_registration", schema = "regprc")
public class QcuserRegistrationIdEntity extends BasePacketEntity<QcuserRegistrationIdPKEntity> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	@Column(name = "cr_dtimes", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimes;

	@Column(name = "del_dtimes")
	@UpdateTimestamp
	private LocalDateTime delDtimes;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	private String status_code;

	@Column(name = "upd_by")
	private String updBy;

	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private LocalDateTime updDtimes;

	public QcuserRegistrationIdEntity() {
		super();
	}

	public String getCrBy() {
		return this.crBy;
	}

	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	public LocalDateTime getCrDtimesz() {
		return this.crDtimes;
	}

	public void setCrDtimesz(LocalDateTime crDtimesz) {
		this.crDtimes = crDtimesz;
	}

	public LocalDateTime getDelDtimesz() {
		return this.delDtimes;
	}

	public void setDelDtimesz(LocalDateTime delDtimesz) {
		this.delDtimes = delDtimesz;
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

	public String getStatus() {
		return this.status_code;
	}

	public void setStatus(String status) {
		this.status_code = status;
	}

	public String getUpdBy() {
		return this.updBy;
	}

	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	public LocalDateTime getUpdDtimesz() {
		return this.updDtimes;
	}

	public void setUpdDtimesz(LocalDateTime updDtimesz) {
		this.updDtimes = updDtimesz;
	}

}