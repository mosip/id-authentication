package io.mosip.registration.processor.quality.check.entity;
	
import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;


/**
 * The Class RoleListEntity.
 *
 * @author M1048399 The persistent class for the role_list database table.
 */
@Entity
@Table(name = "role_list", schema = "master")
public class RoleListEntity extends BaseQcuserEntity<RoleListPKEntity> implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The cr by. */
	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	/** The cr dtimesz. */
	@Column(name = "cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	/** The del dtimesz. */
	@Column(name = "del_dtimesz")
	@CreationTimestamp
	private LocalDateTime delDtimesz;

	/** The descr. */
	private String descr;

	/** The is active. */
	@Column(name = "is_active")
	private Boolean isActive;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	/** The upd dtimesz. */
	@Column(name = "upd_dtimesz")
	@CreationTimestamp
	private LocalDateTime updDtimesz;

	/**
	 * Instantiates a new role list entity.
	 */
	public RoleListEntity() {
		super();
	}

	/**
	 * Gets the cr by.
	 *
	 * @return the cr by
	 */
	public String getCrBy() {
		return this.crBy;
	}

	/**
	 * Sets the cr by.
	 *
	 * @param crBy the new cr by
	 */
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	/**
	 * Gets the cr dtimesz.
	 *
	 * @return the cr dtimesz
	 */
	public LocalDateTime getCrDtimesz() {
		return this.crDtimesz;
	}

	/**
	 * Sets the cr dtimesz.
	 *
	 * @param crDtimesz the new cr dtimesz
	 */
	public void setCrDtimesz(LocalDateTime crDtimesz) {
		this.crDtimesz = crDtimesz;
	}

	/**
	 * Gets the del dtimesz.
	 *
	 * @return the del dtimesz
	 */
	public LocalDateTime getDelDtimesz() {
		return this.delDtimesz;
	}

	/**
	 * Sets the del dtimesz.
	 *
	 * @param delDtimesz the new del dtimesz
	 */
	public void setDelDtimesz(LocalDateTime delDtimesz) {
		this.delDtimesz = delDtimesz;
	}

	/**
	 * Gets the descr.
	 *
	 * @return the descr
	 */
	public String getDescr() {
		return this.descr;
	}

	/**
	 * Sets the descr.
	 *
	 * @param descr the new descr
	 */
	public void setDescr(String descr) {
		this.descr = descr;
	}

	/**
	 * Gets the checks if is active.
	 *
	 * @return the checks if is active
	 */
	public Boolean getIsActive() {
		return this.isActive;
	}

	/**
	 * Sets the checks if is active.
	 *
	 * @param isActive the new checks if is active
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Gets the checks if is deleted.
	 *
	 * @return the checks if is deleted
	 */
	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	/**
	 * Sets the checks if is deleted.
	 *
	 * @param isDeleted the new checks if is deleted
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * Gets the upd by.
	 *
	 * @return the upd by
	 */
	public String getUpdBy() {
		return this.updBy;
	}

	/**
	 * Sets the upd by.
	 *
	 * @param updBy the new upd by
	 */
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	/**
	 * Gets the upd dtimesz.
	 *
	 * @return the upd dtimesz
	 */
	public LocalDateTime getUpdDtimesz() {
		return this.updDtimesz;
	}

	/**
	 * Sets the upd dtimesz.
	 *
	 * @param updDtimesz the new upd dtimesz
	 */
	public void setUpdDtimesz(LocalDateTime updDtimesz) {
		this.updDtimesz = updDtimesz;
	}

}