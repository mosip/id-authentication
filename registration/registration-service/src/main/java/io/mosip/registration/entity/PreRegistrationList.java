package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Pre Registration entity
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "pre_registration", schema = "reg")
public class PreRegistration extends RegistrationCommonFields{
	@Id
	@Column(name = "pre_reg_id", length = 36, nullable = false)	
	private String id;
	@Column(name = "reg_id", length = 36, nullable = true)
	private String regId;
	@Column(name = "file_path", length = 128, nullable = true)
	private String filePath;
	@Column(name = "session_key", length = 128, nullable = true)
	private String sessionKey;
	@Column(name = "status_code", length = 36, nullable = false)
	private String statusCode;
	@Column(name = "status_comments", length = 1024, nullable = true)
	private String statusComments;
	@Column(name = "lang_code", length = 3, nullable = false)
	private String langCode;
	@Column(name = "is_deleted", nullable = true)
	private Boolean isDeleted;
	@Column(name = "del_dtimes", nullable = true)
	private String delDtimes;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusComments() {
		return statusComments;
	}
	public void setStatusComments(String statusComments) {
		this.statusComments = statusComments;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getDelDtimes() {
		return delDtimes;
	}
	public void setDelDtimes(String delDtimes) {
		this.delDtimes = delDtimes;
	}
	

}
