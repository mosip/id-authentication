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
	private static final long serialVersionUID = 1L;

	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	@Column(name = "cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	@Column(name = "del_dtimesz")
	@UpdateTimestamp
	private LocalDateTime delDtimesz;

	@Column(name = "image_name", nullable = false)
	private String imageName;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "no_of_retry")
	private Integer noOfRetry;

	@Column(name = "prereg_id", nullable = false)
	private String preRegId;

	@Column(name = "quality_score")
	private BigDecimal qualityScore;

	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	@Column(name = "upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updDtimesz;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public ApplicantFingerprintEntity() {
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

	public String getImageName() {
		return this.imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Integer getNoOfRetry() {
		return this.noOfRetry;
	}

	public void setNoOfRetry(Integer noOfRetry) {
		this.noOfRetry = noOfRetry;
	}

	public String getPreRegId() {
		return this.preRegId;
	}

	public void setPreRegId(String preregId) {
		this.preRegId = preregId;
	}

	public BigDecimal getQualityScore() {
		return this.qualityScore;
	}

	public void setQualityScore(BigDecimal qualityScore) {
		this.qualityScore = qualityScore;
	}

	public String getStatusCode() {
		return this.statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
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