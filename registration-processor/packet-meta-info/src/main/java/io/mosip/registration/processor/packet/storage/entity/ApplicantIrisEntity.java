package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


/**
 * The persistent class for the applicant_iris database table.
 * 
 * @author Horteppa M1048399
 */
@Entity
@Table(name="applicant_iris", schema = "regprc")
public class ApplicantIrisEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ApplicantIrisPKEntity id;

	@Column(name="cr_by")
	private String crBy = "MOSIP_SYSTEM";

	@Column(name="cr_dtimesz", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimesz;

	@Column(name="del_dtimesz")
	@UpdateTimestamp
	private LocalDateTime delDtimesz;

	@Column(name="image_name")
	private String imageName;

	@Column(name="is_deleted")
	private Boolean isDeleted;

	@Column(name="no_of_retry")
	private Integer noOfRetry;

	@Column(name="prereg_id")
	private String preregId;

	@Column(name="quality_score")
	private BigDecimal qualityScore;

	@Column(name="status_code")
	private String statusCode;

	@Column(name="upd_by")
	private String updBy = "MOSIP_SYSTEM";

	@Column(name="upd_dtimesz")
	@UpdateTimestamp
	private LocalDateTime updDtimesz;

	public ApplicantIrisEntity() {
		super();
	}

	public ApplicantIrisPKEntity getId() {
		return this.id;
	}

	public void setId(ApplicantIrisPKEntity id) {
		this.id = id;
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

	public String getPreregId() {
		return this.preregId;
	}

	public void setPreregId(String preregId) {
		this.preregId = preregId;
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