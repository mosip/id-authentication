package io.mosip.registration.processor.manual.adjudication.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the reg_manual_verification database table.
 * 
 */
@Entity
@Table(name="reg_manual_verification", schema="regprc")
public class ManualVerificationEntity implements Serializable{
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ManualVerificationPKEntity pkId;

	@Column(name="cr_by")
	private String crBy;

	@Column(name="cr_dtimes")
	private Timestamp crDtimes;

	@Column(name="del_dtimes")
	private Timestamp delDtimes;

	@Column(name="is_active")
	private Boolean isActive;

	@Column(name="is_deleted")
	private Boolean isDeleted;

	@Column(name="lang_code")
	private String langCode;

	@Column(name="matched_score")
	private BigDecimal matchedScore;

	@Column(name="mv_usr_id")
	private String mvUsrId;

	@Column(name="reason_code")
	private String reasonCode;

	@Column(name="status_code")
	private String statusCode;

	@Column(name="status_comment")
	private String statusComment;

	@Column(name="upd_by")
	private String updBy;

	@Column(name="upd_dtimes")
	private Timestamp updDtimes;

	public ManualVerificationPKEntity getId() {
		return this.pkId;
	}

	public void setId(ManualVerificationPKEntity id) {
		this.pkId = id;
	}

	public String getCrBy() {
		return this.crBy;
	}

	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	public Timestamp getCrDtimes() {
		return this.crDtimes;
	}

	public void setCrDtimes(Timestamp crDtimes) {
		this.crDtimes = crDtimes;
	}

	public Timestamp getDelDtimes() {
		return this.delDtimes;
	}

	public void setDelDtimes(Timestamp delDtimes) {
		this.delDtimes = delDtimes;
	}

	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getLangCode() {
		return this.langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public BigDecimal getMatchedScore() {
		return this.matchedScore;
	}

	public void setMatchedScore(BigDecimal matchedScore) {
		this.matchedScore = matchedScore;
	}

	public String getMvUsrId() {
		return this.mvUsrId;
	}

	public void setMvUsrId(String mvUsrId) {
		this.mvUsrId = mvUsrId;
	}

	public String getReasonCode() {
		return this.reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getStatusCode() {
		return this.statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusComment() {
		return this.statusComment;
	}

	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	public String getUpdBy() {
		return this.updBy;
	}

	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	public Timestamp getUpdDtimes() {
		return this.updDtimes;
	}

	public void setUpdDtimes(Timestamp updDtimes) {
		this.updDtimes = updDtimes;
	}

}