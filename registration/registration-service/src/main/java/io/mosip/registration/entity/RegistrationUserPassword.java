package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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

	@OneToOne
	@JoinColumn(name = "usr_id", nullable = false, insertable = false, updatable = false)
	private RegistrationUserDetail registrationUserDetail;

	@Id
	@Column(name = "usr_id")
	private String usrId;
	@Column(name = "pwd")
	private String pwd;
	@Column(name = "pwd_expiry_dtimes")
	private Timestamp pwdExpiryDtimes;
	@Column(name = "status_code")
	private String statusCode;
	@Column(name = "lang_code")
	private String langCode;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

	/**
	 * @return the registrationUserDetail
	 */
	public RegistrationUserDetail getRegistrationUserDetail() {
		return registrationUserDetail;
	}

	/**
	 * @param registrationUserDetail
	 *            the registrationUserDetail to set
	 */
	public void setRegistrationUserDetail(RegistrationUserDetail registrationUserDetail) {
		this.registrationUserDetail = registrationUserDetail;
	}

	/**
	 * @return the usrId
	 */
	public String getUsrId() {
		return usrId;
	}

	/**
	 * @param usrId
	 *            the usrId to set
	 */
	public void setUsrId(String usrId) {
		this.usrId = usrId;
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

}
