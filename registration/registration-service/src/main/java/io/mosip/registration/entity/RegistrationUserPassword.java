package io.mosip.registration.entity;

import java.sql.Timestamp;

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
	private RegistrationUserPasswordId registrationUserPasswordId;

	@Column(name = "pwd", length = 512, nullable = false, updatable = false)
	private String pwd;
	@Column(name = "pwd_expiry_dtimes", nullable = true, updatable = false)
	private Timestamp pwdExpiryDtimes;
	@Column(name = "status_code", length = 64, nullable = false, updatable = false)
	private String statusCode;

	/**
	 * @return the registrationUserPasswordId
	 */
	public RegistrationUserPasswordId getRegistrationUserPasswordId() {
		return registrationUserPasswordId;
	}

	/**
	 * @param registrationUserPasswordId
	 *            the registrationUserPasswordId to set
	 */
	public void setRegistrationUserPasswordId(RegistrationUserPasswordId registrationUserPasswordId) {
		this.registrationUserPasswordId = registrationUserPasswordId;
	}

	/**
	 * @return the pwd
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * @param pwd
	 *            the pwd to set
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	/**
	 * @return the pwdExpiryDtimes
	 */
	public Timestamp getPwdExpiryDtimes() {
		return pwdExpiryDtimes;
	}

	/**
	 * @param pwdExpiryDtimes
	 *            the pwdExpiryDtimes to set
	 */
	public void setPwdExpiryDtimes(Timestamp pwdExpiryDtimes) {
		this.pwdExpiryDtimes = pwdExpiryDtimes;
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

}
