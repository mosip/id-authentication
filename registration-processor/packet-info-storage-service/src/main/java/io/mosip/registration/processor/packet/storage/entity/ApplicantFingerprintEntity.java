package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The persistent class for the applicant_fingerprint database table.
 * 
 * @author Horteppa M1048399
 */
@Entity
@Table(name = "applicant_fingerprint", schema = "regprc")
public class ApplicantFingerprintEntity extends BasePacketEntity<ApplicantFingerprintPKEntity> implements Serializable {

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
	@UpdateTimestamp
	private LocalDateTime delDtimesz;

	/** The image name. */
	@Column(name = "image_name", nullable = false)
	private String imageName;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The no of retry. */
	@Column(name = "no_of_retry")
	private Integer noOfRetry;

	/** The pre reg id. */
	@Column(name = "prereg_id", nullable = false)
	private String preRegId;

	/** The quality score. */
	@Column(name = "quality_score")
	private BigDecimal qualityScore;

	/** The status code. */
	@Column(name = "status_code")
	private String statusCode;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	/** The upd dtimesz. */
	@Column(name = "upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updDtimesz;

	/** The is active. */
	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	/**
	 * Checks if is active.
	 *
	 * @return true, if is active
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Sets the active.
	 *
	 * @param isActive the new active
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Instantiates a new applicant fingerprint entity.
	 */
	public ApplicantFingerprintEntity() {
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
	 * Gets the image name.
	 *
	 * @return the image name
	 */
	public String getImageName() {
		return this.imageName;
	}

	/**
	 * Sets the image name.
	 *
	 * @param imageName the new image name
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
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
	 * Gets the no of retry.
	 *
	 * @return the no of retry
	 */
	public Integer getNoOfRetry() {
		return this.noOfRetry;
	}

	/**
	 * Sets the no of retry.
	 *
	 * @param noOfRetry the new no of retry
	 */
	public void setNoOfRetry(Integer noOfRetry) {
		this.noOfRetry = noOfRetry;
	}

	/**
	 * Gets the pre reg id.
	 *
	 * @return the pre reg id
	 */
	public String getPreRegId() {
		return this.preRegId;
	}

	/**
	 * Sets the pre reg id.
	 *
	 * @param preregId the new pre reg id
	 */
	public void setPreRegId(String preregId) {
		this.preRegId = preregId;
	}

	/**
	 * Gets the quality score.
	 *
	 * @return the quality score
	 */
	public BigDecimal getQualityScore() {
		return this.qualityScore;
	}

	/**
	 * Sets the quality score.
	 *
	 * @param qualityScore the new quality score
	 */
	public void setQualityScore(BigDecimal qualityScore) {
		this.qualityScore = qualityScore;
	}

	/**
	 * Gets the status code.
	 *
	 * @return the status code
	 */
	public String getStatusCode() {
		return this.statusCode;
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