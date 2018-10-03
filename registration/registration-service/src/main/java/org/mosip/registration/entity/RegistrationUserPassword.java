package org.mosip.registration.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * RegistrationUserPassword entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "user_pwd")
public class RegistrationUserPassword extends RegistrationCommonFields {
	@EmbeddedId
	private RegistrationUserPasswordID registrationUserPasswordID;

	@Column(name = "pwd_expiry_dtimes", nullable = false, updatable = false)
	private OffsetDateTime pwdExpiryDtimes;
	@Column(name = "status_code", length = 64, nullable = true, updatable = false)
	private String statusCode;
	@Column(name = "lang_code", length = 3, nullable = false, updatable = false)
	private String langCode;
	
	public RegistrationUserPasswordID getRegistrationUserPasswordID() {
		return registrationUserPasswordID;
	}
	public void setRegistrationUserPasswordID(RegistrationUserPasswordID registrationUserPasswordID) {
		this.registrationUserPasswordID = registrationUserPasswordID;
	}
	public OffsetDateTime getPwdExpiryDtimes() {
		return pwdExpiryDtimes;
	}
	public void setPwdExpiryDtimes(OffsetDateTime pwdExpiryDtimes) {
		this.pwdExpiryDtimes = pwdExpiryDtimes;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	
}
