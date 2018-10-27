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
	private static final long serialVersionUID = 1L;

	@Column(name = "prereg_id", nullable = false)
	private String preRegId;

	@Column(name = "image_name", nullable = false)
	private String imageName;

	@Column(name = "quality_score")
	private BigDecimal qualityScore;

	@Column(name = "no_of_retry")
	private Integer noOfRetry;

	@Column(name = "image_store")
	private byte[] imageStore;

	@Column(name = "has_excp_photograph")
	private Boolean hasExcpPhotograph;

	@Column(name = "excp_photo_name", nullable = false)
	private String excpPhotoName;

	@Column(name = "excp_photo_store")
	private byte[] excpPhotoStore;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	@Column(name = "cr_by", nullable = false)
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

	public String getPreRegId() {
		return preRegId;
	}

	public void setPreRegId(String preRegId) {
		this.preRegId = preRegId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public BigDecimal getQualityScore() {
		return qualityScore;
	}

	public void setQualityScore(BigDecimal qualityScore) {
		this.qualityScore = qualityScore;
	}

	public Integer getNoOfRetry() {
		return noOfRetry;
	}

	public void setNoOfRetry(Integer noOfRetry) {
		this.noOfRetry = noOfRetry;
	}

	public byte[] getImageStore() {
		return imageStore;
	}

	public void setImageStore(byte[] imageStore) {
		this.imageStore = imageStore;
	}

	public Boolean getHasExcpPhotograph() {
		return hasExcpPhotograph;
	}

	public void setHasExcpPhotograph(Boolean hasExcpPhotograph) {
		this.hasExcpPhotograph = hasExcpPhotograph;
	}

	public String getExcpPhotoName() {
		return excpPhotoName;
	}

	public void setExcpPhotoName(String excpPhotoName) {
		this.excpPhotoName = excpPhotoName;
	}

	public byte[] getExcpPhotoStore() {
		return excpPhotoStore;
	}

	public void setExcpPhotoStore(byte[] excpPhotoStore) {
		this.excpPhotoStore = excpPhotoStore;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
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
}