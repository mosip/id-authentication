package io.mosip.registration.processor.manual.adjudication.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * The persistent class for the reg_manual_verification database table.
 * 
 * @author Shuchita
 * @since 0.0.1
 *
 */
@Entity
@Table(name = "reg_manual_verification", schema = "regprc")
public class ManualVerificationEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ManualVerificationPKEntity pkId;

	@Column(name = "cr_by")
	private String crBy;

	@Column(name = "cr_dtimes")
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
	private Timestamp updDtimes;

	/**
	 * @return the pkId
	 */
	public ManualVerificationPKEntity getPkId() {
		return pkId;
	}

	/**
	 * @param pkId the pkId to set
	 */
	public void setPkId(ManualVerificationPKEntity pkId) {
		this.pkId = pkId;
	}

	/**
	 * @return the crBy
	 */
	public String getCrBy() {
		return crBy;
	}

	/**
	 * @param crBy the crBy to set
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
	 * @param crDtimes the crDtimes to set
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
	 * @param delDtimes the delDtimes to set
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
	 * @param isActive the isActive to set
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
	 * @param isDeleted the isDeleted to set
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
	 * @param langCode the langCode to set
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
	 * @param matchedScore the matchedScore to set
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
	 * @param mvUsrId the mvUsrId to set
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
	 * @param reasonCode the reasonCode to set
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
	 * @param statusCode the statusCode to set
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
	 * @param statusComment the statusComment to set
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
	 * @param updBy the updBy to set
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
	 * @param updDtimes the updDtimes to set
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((crBy == null) ? 0 : crBy.hashCode());
		result = prime * result + ((crDtimes == null) ? 0 : crDtimes.hashCode());
		result = prime * result + ((delDtimes == null) ? 0 : delDtimes.hashCode());
		result = prime * result + ((isActive == null) ? 0 : isActive.hashCode());
		result = prime * result + ((isDeleted == null) ? 0 : isDeleted.hashCode());
		result = prime * result + ((langCode == null) ? 0 : langCode.hashCode());
		result = prime * result + ((matchedScore == null) ? 0 : matchedScore.hashCode());
		result = prime * result + ((mvUsrId == null) ? 0 : mvUsrId.hashCode());
		result = prime * result + ((pkId == null) ? 0 : pkId.hashCode());
		result = prime * result + ((reasonCode == null) ? 0 : reasonCode.hashCode());
		result = prime * result + ((statusCode == null) ? 0 : statusCode.hashCode());
		result = prime * result + ((statusComment == null) ? 0 : statusComment.hashCode());
		result = prime * result + ((updBy == null) ? 0 : updBy.hashCode());
		result = prime * result + ((updDtimes == null) ? 0 : updDtimes.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ManualVerificationEntity other = (ManualVerificationEntity) obj;
		if (crBy == null) {
			if (other.crBy != null)
				return false;
		} else if (!crBy.equals(other.crBy))
			return false;
		if (crDtimes == null) {
			if (other.crDtimes != null)
				return false;
		} else if (!crDtimes.equals(other.crDtimes))
			return false;
		if (delDtimes == null) {
			if (other.delDtimes != null)
				return false;
		} else if (!delDtimes.equals(other.delDtimes))
			return false;
		if (isActive == null) {
			if (other.isActive != null)
				return false;
		} else if (!isActive.equals(other.isActive))
			return false;
		if (isDeleted == null) {
			if (other.isDeleted != null)
				return false;
		} else if (!isDeleted.equals(other.isDeleted))
			return false;
		if (langCode == null) {
			if (other.langCode != null)
				return false;
		} else if (!langCode.equals(other.langCode))
			return false;
		if (matchedScore == null) {
			if (other.matchedScore != null)
				return false;
		} else if (!matchedScore.equals(other.matchedScore))
			return false;
		if (mvUsrId == null) {
			if (other.mvUsrId != null)
				return false;
		} else if (!mvUsrId.equals(other.mvUsrId))
			return false;
		if (pkId == null) {
			if (other.pkId != null)
				return false;
		} else if (!pkId.equals(other.pkId))
			return false;
		if (reasonCode == null) {
			if (other.reasonCode != null)
				return false;
		} else if (!reasonCode.equals(other.reasonCode))
			return false;
		if (statusCode == null) {
			if (other.statusCode != null)
				return false;
		} else if (!statusCode.equals(other.statusCode))
			return false;
		if (statusComment == null) {
			if (other.statusComment != null)
				return false;
		} else if (!statusComment.equals(other.statusComment))
			return false;
		if (updBy == null) {
			if (other.updBy != null)
				return false;
		} else if (!updBy.equals(other.updBy))
			return false;
		if (updDtimes == null) {
			if (other.updDtimes != null)
				return false;
		} else if (!updDtimes.equals(other.updDtimes))
			return false;
		return true;
	}
	
	
	
	
	
	

}