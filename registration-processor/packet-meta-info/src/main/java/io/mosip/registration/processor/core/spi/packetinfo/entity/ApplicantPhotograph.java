package io.mosip.registration.processor.core.spi.packetinfo.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the applicant_photograph database table.
 * 
 */
@Entity
@Table(name="applicant_photograph")
@NamedQuery(name="ApplicantPhotograph.findAll", query="SELECT a FROM ApplicantPhotograph a")
public class ApplicantPhotograph implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ApplicantPhotographPK id;

	@Column(name="cr_by")
	private String crBy;

	@Column(name="cr_dtimesz")
	private Timestamp crDtimesz;

	@Column(name="del_dtimesz")
	private Timestamp delDtimesz;

	@Column(name="excp_photo_name")
	private String excpPhotoName;

	@Column(name="has_excp_photograph")
	private Boolean hasExcpPhotograph;

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
	private String updBy;

	@Column(name="upd_dtimesz")
	private Timestamp updDtimesz;

	public ApplicantPhotograph() {
		super();
	}

	public ApplicantPhotographPK getId() {
		return this.id;
	}

	public void setId(ApplicantPhotographPK id) {
		this.id = id;
	}

	public String getCrBy() {
		return this.crBy;
	}

	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	public Timestamp getCrDtimesz() {
		return this.crDtimesz;
	}

	public void setCrDtimesz(Timestamp crDtimesz) {
		this.crDtimesz = crDtimesz;
	}

	public Timestamp getDelDtimesz() {
		return this.delDtimesz;
	}

	public void setDelDtimesz(Timestamp delDtimesz) {
		this.delDtimesz = delDtimesz;
	}

	public String getExcpPhotoName() {
		return this.excpPhotoName;
	}

	public void setExcpPhotoName(String excpPhotoName) {
		this.excpPhotoName = excpPhotoName;
	}

	public Boolean getHasExcpPhotograph() {
		return this.hasExcpPhotograph;
	}

	public void setHasExcpPhotograph(Boolean hasExcpPhotograph) {
		this.hasExcpPhotograph = hasExcpPhotograph;
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

	public Timestamp getUpdDtimesz() {
		return this.updDtimesz;
	}

	public void setUpdDtimesz(Timestamp updDtimesz) {
		this.updDtimesz = updDtimesz;
	}

}