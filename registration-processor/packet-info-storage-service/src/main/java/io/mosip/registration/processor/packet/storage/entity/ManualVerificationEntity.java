package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The persistent class for the reg_manual_verification database table.
 * 
 * @author Shuchita
 * @since 0.0.1
 *
 */
@Entity
@Table(name = "reg_manual_verification", schema = "regprc")
public class ManualVerificationEntity extends BasePacketEntity<ManualVerificationPKEntity> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "cr_by")
	private String crBy = "SYSTEM";

	@Column(name = "cr_dtimes")
	@CreationTimestamp
	private Timestamp crDtimes;

	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "lang_code")
	private String langCode;

	@Column(name = "matched_score")
	private BigDecimal matchedScore;

	@Column(name = "mv_usr_id")
	private String mvUsrId;

	@Column(name = "reason_code")
	private String reasonCode;

	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "status_comment")
	private String statusComment;

	@Column(name = "upd_by")
	private String updBy;

	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private Timestamp updDtimes;

	/**
	 * @return the crBy
	 */
	public String getCrBy() {
		return crBy;
	}

	/**
	 * @param crBy
	 *            the crBy to set
	 */
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	/**
	 * @return the crDtimes
	 */
	public Timestamp getCrDtimes() {
		return crDtimes;
	}

	/**
	 * @param crDtimes
	 *            the crDtimes to set
	 */
	public void setCrDtimes(Timestamp crDtimes) {
		this.crDtimes = crDtimes;
	}

	/**
	 * @return the delDtimes
	 */
	public Timestamp getDelDtimes() {
		return delDtimes;
	}

	/**
	 * @param delDtimes
	 *            the delDtimes to set
	 */
	public void setDelDtimes(Timestamp delDtimes) {
		this.delDtimes = delDtimes;
	}

	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive
	 *            the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * @param langCode
	 *            the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * @return the matchedScore
	 */
	public BigDecimal getMatchedScore() {
		return matchedScore;
	}

	/**
	 * @param matchedScore
	 *            the matchedScore to set
	 */
	public void setMatchedScore(BigDecimal matchedScore) {
		this.matchedScore = matchedScore;
	}

	/**
	 * @return the mvUsrId
	 */
	public String getMvUsrId() {
		return mvUsrId;
	}

	/**
	 * @param mvUsrId
	 *            the mvUsrId to set
	 */
	public void setMvUsrId(String mvUsrId) {
		this.mvUsrId = mvUsrId;
	}

	/**
	 * @return the reasonCode
	 */
	public String getReasonCode() {
		return reasonCode;
	}

	/**
	 * @param reasonCode
	 *            the reasonCode to set
	 */
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode
	 *            the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the statusComment
	 */
	public String getStatusComment() {
		return statusComment;
	}

	/**
	 * @param statusComment
	 *            the statusComment to set
	 */
	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	/**
	 * @return the updBy
	 */
	public String getUpdBy() {
		return updBy;
	}

	/**
	 * @param updBy
	 *            the updBy to set
	 */
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	/**
	 * @return the updDtimes
	 */
	public Timestamp getUpdDtimes() {
		return updDtimes;
	}

	/**
	 * @param updDtimes
	 *            the updDtimes to set
	 */
	public void setUpdDtimes(Timestamp updDtimes) {
		this.updDtimes = updDtimes;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
