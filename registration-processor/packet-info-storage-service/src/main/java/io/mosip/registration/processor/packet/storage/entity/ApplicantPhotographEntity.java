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
 * The persistent class for the applicant_photograph database table.
 * 
 * @author Horteppa M1048399
 */
@Entity
@Table(name = "applicant_photograph", schema = "regprc")
public class ApplicantPhotographEntity extends BasePacketEntity<ApplicantPhotographPKEntity> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The pre reg id. */
	@Column(name = "prereg_id", nullable = false)
	private String preRegId;

	/** The image name. */
	@Column(name = "image_name", nullable = false)
	private String imageName;

	/** The quality score. */
	@Column(name = "quality_score")
	private BigDecimal qualityScore;

	/** The no of retry. */
	@Column(name = "no_of_retry")
	private Integer noOfRetry;

	/** The image store. */
	@Column(name = "image_store")
	private byte[] imageStore;

	/** The has excp photograph. */
	@Column(name = "has_excp_photograph")
	private Boolean hasExcpPhotograph;

	/** The excp photo name. */
	@Column(name = "excp_photo_name", nullable = false)
	private String excpPhotoName;

	/** The excp photo store. */
	@Column(name = "excp_photo_store")
	private byte[] excpPhotoStore;

	/** The is active. */
	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	/** The cr by. */
	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	/** The cr dtimes. */
	@Column(name = "cr_dtimes", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimes;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	/** The upd dtimes. */
	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private LocalDateTime updDtimes;

	/** The del dtimes. */
	@Column(name = "del_dtimes")
	@UpdateTimestamp
	private LocalDateTime delDtimes;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

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
	 * Gets the image name.
	 *
	 * @return the image name
	 */
	public String getImageName() {
		return imageName;
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
	 * Gets the quality score.
	 *
	 * @return the quality score
	 */
	public BigDecimal getQualityScore() {
		return qualityScore;
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
	 * Gets the no of retry.
	 *
	 * @return the no of retry
	 */
	public Integer getNoOfRetry() {
		return noOfRetry;
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
	 * Gets the image store.
	 *
	 * @return the image store
	 */
	public byte[] getImageStore() {
		return imageStore;
	}

	/**
	 * Sets the image store.
	 *
	 * @param imageStore the new image store
	 */
	public void setImageStore(byte[] imageStore) {
		this.imageStore = imageStore;
	}

	/**
	 * Gets the checks for excp photograph.
	 *
	 * @return the checks for excp photograph
	 */
	public Boolean getHasExcpPhotograph() {
		return hasExcpPhotograph;
	}

	/**
	 * Sets the checks for excp photograph.
	 *
	 * @param hasExcpPhotograph the new checks for excp photograph
	 */
	public void setHasExcpPhotograph(Boolean hasExcpPhotograph) {
		this.hasExcpPhotograph = hasExcpPhotograph;
	}

	/**
	 * Gets the excp photo name.
	 *
	 * @return the excp photo name
	 */
	public String getExcpPhotoName() {
		return excpPhotoName;
	}

	/**
	 * Sets the excp photo name.
	 *
	 * @param excpPhotoName the new excp photo name
	 */
	public void setExcpPhotoName(String excpPhotoName) {
		this.excpPhotoName = excpPhotoName;
	}

	/**
	 * Gets the excp photo store.
	 *
	 * @return the excp photo store
	 */
	public byte[] getExcpPhotoStore() {
		return excpPhotoStore;
	}

	/**
	 * Sets the excp photo store.
	 *
	 * @param excpPhotoStore the new excp photo store
	 */
	public void setExcpPhotoStore(byte[] excpPhotoStore) {
		this.excpPhotoStore = excpPhotoStore;
	}

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
}