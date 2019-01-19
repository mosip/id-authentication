package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The persistent class for the biometric_exceptions database table.
 * 
 * @author Horteppa M1048399
 */
@Entity
@Table(name = "biometric_exception", schema = "regprc")
public class BiometricExceptionEntity extends BasePacketEntity<BiometricExceptionPKEntity> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The prereg id. */
	@Column(name = "prereg_id", nullable = false)
	private String preregId;

	/** The bio typ. */
	@Column(name = "bio_typ", nullable = false)
	private String bioTyp;

	/** The excp descr. */
	@Column(name = "excp_descr")
	private String excpDescr;

	/** The excp typ. */
	@Column(name = "excp_typ")
	private String excpTyp;

	/** The status code. */
	@Column(name = "status_code")
	private String statusCode;

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

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	/** The upd dtimes. */
	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private LocalDateTime updDtimes;

	/**
	 * Instantiates a new biometric exception entity.
	 */
	public BiometricExceptionEntity() {
		super();
	}

	/**
	 * Gets the prereg id.
	 *
	 * @return the prereg id
	 */
	public String getPreregId() {
		return preregId;
	}

	/**
	 * Sets the prereg id.
	 *
	 * @param preregId
	 *            the new prereg id
	 */
	public void setPreregId(String preregId) {
		this.preregId = preregId;
	}

	/**
	 * Gets the bio typ.
	 *
	 * @return the bio typ
	 */
	public String getBioTyp() {
		return bioTyp;
	}

	/**
	 * Sets the bio typ.
	 *
	 * @param bioTyp
	 *            the new bio typ
	 */
	public void setBioTyp(String bioTyp) {
		this.bioTyp = bioTyp;
	}

	/**
	 * Gets the excp descr.
	 *
	 * @return the excp descr
	 */
	public String getExcpDescr() {
		return excpDescr;
	}

	/**
	 * Sets the excp descr.
	 *
	 * @param excpDescr
	 *            the new excp descr
	 */
	public void setExcpDescr(String excpDescr) {
		this.excpDescr = excpDescr;
	}

	/**
	 * Gets the excp typ.
	 *
	 * @return the excp typ
	 */
	public String getExcpTyp() {
		return excpTyp;
	}

	/**
	 * Sets the excp typ.
	 *
	 * @param excpTyp
	 *            the new excp typ
	 */
	public void setExcpTyp(String excpTyp) {
		this.excpTyp = excpTyp;
	}

	/**
	 * Gets the status code.
	 *
	 * @return the status code
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the status code.
	 *
	 * @param statusCode
	 *            the new status code
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the cr by.
	 *
	 * @return the cr by
	 */
	public String getCrBy() {
		return crBy;
	}

	/**
	 * Sets the cr by.
	 *
	 * @param crBy
	 *            the new cr by
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
		return crDtimes;
	}

	/**
	 * Sets the cr dtimes.
	 *
	 * @param crDtimes
	 *            the new cr dtimes
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
		return delDtimes;
	}

	/**
	 * Sets the del dtimes.
	 *
	 * @param delDtimes
	 *            the new del dtimes
	 */
	public void setDelDtimes(LocalDateTime delDtimes) {
		this.delDtimes = delDtimes;
	}

	/**
	 * Gets the checks if is deleted.
	 *
	 * @return the checks if is deleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * Sets the checks if is deleted.
	 *
	 * @param isDeleted
	 *            the new checks if is deleted
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
		return updBy;
	}

	/**
	 * Sets the upd by.
	 *
	 * @param updBy
	 *            the new upd by
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
		return updDtimes;
	}

	/**
	 * Sets the upd dtimes.
	 *
	 * @param updDtimes
	 *            the new upd dtimes
	 */
	public void setUpdDtimes(LocalDateTime updDtimes) {
		this.updDtimes = updDtimes;
	}

}