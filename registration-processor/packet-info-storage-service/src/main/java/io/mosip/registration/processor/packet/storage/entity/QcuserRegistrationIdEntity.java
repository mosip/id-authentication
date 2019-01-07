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
@Table(name="qcuser_registration", schema = "regprc")
public class QcuserRegistrationIdEntity extends BasePacketEntity<QcuserRegistrationIdPKEntity> implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The cr by. */
	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	/** The cr dtimes. */
	@Column(name = "cr_dtimes", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimes;


	/** The del dtimes. */
	@Column(name = "del_dtimes")
	@UpdateTimestamp
	private LocalDateTime delDtimes;

	/** The is active. */
	@Column(name = "is_active")
	private Boolean isActive;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The status code. */
	private String status_code;

	/** The status comment. */
	private String status_comment;

	/** The lang code. */
	private String lang_code;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy;

	/** The upd dtimes. */
	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private LocalDateTime updDtimes;

	/**
	 * Instantiates a new qcuser registration id entity.
	 */
	public QcuserRegistrationIdEntity() {
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
	 * Gets the cr dtimes.
	 *
	 * @return the cr dtimes
	 */
	public LocalDateTime getCrDtimes() {
		return this.crDtimes;
	}

	/**
	 * Sets the cr dtimes.
	 *
	 * @param crDtimes the new cr dtimes
	 */
	public void setCrDtimes(LocalDateTime crDtimes) {
		this.crDtimes = crDtimes;
	}

	/**
	 * Gets the del dtimes.
	 *
	 * @return the del dtimes
	 */
	public LocalDateTime getDelDtimes() {
		return this.delDtimes;
	}

	/**
	 * Sets the del dtimes.
	 *
	 * @param delDtimes the new del dtimes
	 */
	public void setDelDtimes(LocalDateTime delDtimes) {
		this.delDtimes = delDtimes;
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
	 * Gets the status code.
	 *
	 * @return the status code
	 */
	public String getStatus_code() {
		return status_code;
	}

	/**
	 * Sets the status code.
	 *
	 * @param status_code the new status code
	 */
	public void setStatus_code(String status_code) {
		this.status_code = status_code;
	}

	/**
	 * Gets the status comment.
	 *
	 * @return the status comment
	 */
	public String getStatus_comment() {
		return status_comment;
	}

	/**
	 * Sets the status comment.
	 *
	 * @param status_comment the new status comment
	 */
	public void setStatus_comment(String status_comment) {
		this.status_comment = status_comment;
	}


	/**
	 * Gets the lang code.
	 *
	 * @return the lang code
	 */
	public String getLang_code() {
		return lang_code;
	}

	/**
	 * Sets the lang code.
	 *
	 * @param lang_code the new lang code
	 */
	public void setLang_code(String lang_code) {
		this.lang_code = lang_code;
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
	 * Gets the upd dtimes.
	 *
	 * @return the upd dtimes
	 */
	public LocalDateTime getUpdDtimes() {
		return this.updDtimes;
	}

	/**
	 * Sets the upd dtimes.
	 *
	 * @param updDtimes the new upd dtimes
	 */
	public void setUpdDtimes(LocalDateTime updDtimes) {
		this.updDtimes = updDtimes;
	}

}