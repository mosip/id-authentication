package io.mosip.registration.processor.quality.check.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

/**
 * @author M1048399 The persistent class for the user_role database table.
 */
@Entity
@Table(name = "user_role", schema = "master")
public class UserRoleEntity extends BaseQcuserEntity<UserRolePKEntity> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	@Column(name = "cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	@Column(name = "del_dtimesz")
	@CreationTimestamp
	private LocalDateTime delDtimesz;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	@Column(name = "upd_dtimesz")
	@CreationTimestamp
	private LocalDateTime updDtimesz;

	public UserRoleEntity() {
		super();
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