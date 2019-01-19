package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The Class ApplicantDemographicInfoJsonEntity.
 */
@Entity
@Table(name = "applicant_demographic", schema = "regprc")
public class ApplicantDemographicInfoJsonEntity extends BasePacketEntity<ApplicantDemographicInfoJsonPKEntity>
		implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The pre reg id. */
	@Column(name = "prereg_id", nullable = false)
	private String preRegId;

	/** The status code. */
	@Column(name = "status_code")
	private String statusCode;

	/** The lang code. */
	@Column(name = "lang_code")
	private String langCode;

	/** The is active. */
	@Column(name = "is_active")
	private Boolean isActive;

	/** The cr by. */
	@Column(name = "cr_by", nullable = false)
	private String crBy = "SYSTEM";

	/** The cr dtimes. */
	@Column(name = "cr_dtimes", updatable = false, nullable = false)
	@CreationTimestamp
	private LocalDateTime crDtimes;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	/** The upd dtimes. */
	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private LocalDateTime updDtimes;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The del dtimes. */
	@Column(name = "del_dtimes")
	@UpdateTimestamp
	private LocalDateTime delDtimes;

	/** The demographic details. */
	@Column(name = "demog_detail")
	private byte[] demographicDetails;

	/**
	 * Instantiates a new applicant demographic info json entity.
	 */
	public ApplicantDemographicInfoJsonEntity() {
		super();
	}

	/**
	 * Gets the pre reg id.
	 *
	 * @return the pre reg id
	 */
	public String getPreRegId() {
		return preRegId;
	}

	/**
	 * Sets the pre reg id.
	 *
	 * @param preRegId the new pre reg id
	 */
	public void setPreRegId(String preRegId) {
		this.preRegId = preRegId;
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
	 * @param statusCode the new status code
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the lang code.
	 *
	 * @return the lang code
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * Sets the lang code.
	 *
	 * @param slangCode the new lang code
	 */
	public void setLangCode(String slangCode) {
		this.langCode = slangCode;
	}

	/**
	 * Gets the checks if is active.
	 *
	 * @return the checks if is active
	 */
	public Boolean getIsActive() {
		return isActive;
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
		return crDtimes;
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
		return updDtimes;
	}

	/**
	 * Sets the upd dtimes.
	 *
	 * @param updDtimes the new upd dtimes
	 */
	public void setUpdDtimes(LocalDateTime updDtimes) {
		this.updDtimes = updDtimes;
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
	 * @param isDeleted the new checks if is deleted
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
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
	 * @param delDtimes the new del dtimes
	 */
	public void setDelDtimes(LocalDateTime delDtimes) {
		this.delDtimes = delDtimes;
	}

	/**
	 * Gets the demographic details.
	 *
	 * @return the demographic details
	 */
	public byte[] getDemographicDetails() {
		return demographicDetails;
	}

	/**
	 * Sets the demographic details.
	 *
	 * @param demographicDetails the new demographic details
	 */
	public void setDemographicDetails(byte[] demographicDetails) {
		this.demographicDetails = demographicDetails;
	}

}
