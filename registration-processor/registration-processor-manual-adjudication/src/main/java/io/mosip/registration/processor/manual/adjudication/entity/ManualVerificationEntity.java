package io.mosip.registration.processor.manual.adjudication.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "reg_manual_verification", schema = "regprc")
public class ManualVerificationEntity extends BasePacketEntity<ManualVerificationPKEntity> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "matched_ref_id", nullable = false)
	private String matchedRefId;
	
	@Column(name = "matched_ref_type", nullable = false)
	private String matchedRefType;
	
	@Column(name = "mv_usr_id")
	private String  mvUserId;
	
	@Column(name = "matched_score")
	private String matchedScore;
	
	@Column(name = "status_code")
	private String statusCode;
	
	@Column(name = "reason_code")
	private String reasonCode;
	
	@Column(name = "status_comment", nullable = false)
	private String statusComment;

	@Column(name = "lang_code", nullable = false)
	private String langCode;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	@Column(name = "cr_by", nullable = false)
	private String createdBy;
	
	@Column(name = "cr_dtimes", nullable = false)
	private String createdDateTime;
	
	@Column(name = "upd_by")
	private String updatedBy;
	
	@Column(name = "upd_dtimes")
	private String updatedDateTime;
	
	@Column(name = "is_deleted")
	private boolean isDeleted;
	
	@Column(name = "deltimes")
	private String deletedDateTime;
	
	public String getMatchedRefId() {
		return matchedRefId;
	}

	public void setMatchedRefId(String matchedRefId) {
		this.matchedRefId = matchedRefId;
	}

	public String getMatchedRefType() {
		return matchedRefType;
	}

	public void setMatchedRefType(String matchedRefType) {
		this.matchedRefType = matchedRefType;
	}

	public String getMvUserId() {
		return mvUserId;
	}

	public void setMvUserId(String mvUserId) {
		this.mvUserId = mvUserId;
	}

	public String getMatchedScore() {
		return matchedScore;
	}

	public void setMatchedScore(String matchedScore) {
		this.matchedScore = matchedScore;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getStatusComment() {
		return statusComment;
	}

	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUpdatedDateTime() {
		return updatedDateTime;
	}

	public void setUpdatedDateTime(String updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getDeletedDateTime() {
		return deletedDateTime;
	}

	public void setDeletedDateTime(String deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}


}
