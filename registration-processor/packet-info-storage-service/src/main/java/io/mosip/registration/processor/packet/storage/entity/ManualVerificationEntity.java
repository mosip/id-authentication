package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

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
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The cr by. */
	@Column(name = "cr_by")
	private String crBy;

	/** The cr dtimes. */
	@Column(name = "cr_dtimes", updatable = false, nullable = false)
	@CreationTimestamp
	private Timestamp crDtimes;

	/** The del dtimes. */
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

	/** The is active. */
	@Column(name = "is_active")
	private Boolean isActive;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The lang code. */
	@Column(name = "lang_code")
	private String langCode;

	/** The matched score. */
	@Column(name = "matched_score")
	private BigDecimal matchedScore;

	/** The mv usr id. */
	@Column(name = "mv_usr_id")
	private String mvUsrId;

	/** The reason code. */
	@Column(name = "reason_code")
	private String reasonCode;

	/** The status code. */
	@Column(name = "status_code")
	private String statusCode;

	/** The status comment. */
	@Column(name = "status_comment")
	private String statusComment;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy;

	/** The upd dtimes. */
	@Column(name = "upd_dtimes")
	private Timestamp updDtimes;

	/**
	 * Instantiates a new manual verification entity.
	 */
	public ManualVerificationEntity() {
		super();
	}

	/**
	 * Gets the cr by.
	 *
	 * @return the crBy
	 */
	public String getCrBy() {
		return crBy;
	}

	/**
	 * Sets the cr by.
	 *
	 * @param crBy            the crBy to set
	 */
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	/**
	 * Gets the cr dtimes.
	 *
	 * @return the crDtimes
	 */
	public Timestamp getCrDtimes() {
		return crDtimes;
	}

	/**
	 * Sets the cr dtimes.
	 *
	 * @param crDtimes            the crDtimes to set
	 */
	public void setCrDtimes(Timestamp crDtimes) {
		this.crDtimes =new Timestamp(crDtimes.getTime());
	}

	/**
	 * Gets the del dtimes.
	 *
	 * @return the delDtimes
	 */
	public Timestamp getDelDtimes() {
		return delDtimes;
	}

	/**
	 * Sets the del dtimes.
	 *
	 * @param delDtimes            the delDtimes to set
	 */
	public void setDelDtimes(Timestamp delDtimes) {
		this.delDtimes = delDtimes;
	}

	/**
	 * Gets the checks if is active.
	 *
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * Sets the checks if is active.
	 *
	 * @param isActive            the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Gets the checks if is deleted.
	 *
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * Sets the checks if is deleted.
	 *
	 * @param isDeleted            the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * Gets the lang code.
	 *
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * Sets the lang code.
	 *
	 * @param langCode            the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * Gets the matched score.
	 *
	 * @return the matchedScore
	 */
	public BigDecimal getMatchedScore() {
		return matchedScore;
	}

	/**
	 * Sets the matched score.
	 *
	 * @param matchedScore            the matchedScore to set
	 */
	public void setMatchedScore(BigDecimal matchedScore) {
		this.matchedScore = matchedScore;
	}

	/**
	 * Gets the mv usr id.
	 *
	 * @return the mvUsrId
	 */
	public String getMvUsrId() {
		return mvUsrId;
	}

	/**
	 * Sets the mv usr id.
	 *
	 * @param mvUsrId            the mvUsrId to set
	 */
	public void setMvUsrId(String mvUsrId) {
		this.mvUsrId = mvUsrId;
	}

	/**
	 * Gets the reason code.
	 *
	 * @return the reasonCode
	 */
	public String getReasonCode() {
		return reasonCode;
	}

	/**
	 * Sets the reason code.
	 *
	 * @param reasonCode            the reasonCode to set
	 */
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	/**
	 * Gets the status code.
	 *
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the status code.
	 *
	 * @param statusCode            the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the status comment.
	 *
	 * @return the statusComment
	 */
	public String getStatusComment() {
		return statusComment;
	}

	/**
	 * Sets the status comment.
	 *
	 * @param statusComment            the statusComment to set
	 */
	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	/**
	 * Gets the upd by.
	 *
	 * @return the updBy
	 */
	public String getUpdBy() {
		return updBy;
	}

	/**
	 * Sets the upd by.
	 *
	 * @param updBy            the updBy to set
	 */
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	/**
	 * Gets the upd dtimes.
	 *
	 * @return the updDtimes
	 */
	public Timestamp getUpdDtimes() {
		return updDtimes;
	}

	/**
	 * Sets the upd dtimes.
	 *
	 * @param updDtimes            the updDtimes to set
	 */
	public void setUpdDtimes(Timestamp updDtimes) {
		this.updDtimes = updDtimes;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}