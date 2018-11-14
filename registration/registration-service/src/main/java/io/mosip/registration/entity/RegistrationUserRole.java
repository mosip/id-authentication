package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * RegistrationUserRole entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "user_role")
public class RegistrationUserRole extends RegistrationCommonFields {

	@EmbeddedId
	private RegistrationUserRoleID registrationUserRoleID;

	@Column(name = "lang_code")
	private String langCode;

	@ManyToOne
	@JoinColumn(name = "usr_id", nullable = false, insertable = false, updatable = false)
	private RegistrationUserDetail registrationUserDetail;

	/**
	 * @return the registrationUserRoleId
	 */
	public RegistrationUserRoleID getRegistrationUserRoleID() {
		return registrationUserRoleID;
	}

	/**
	 * @param registrationUserRoleId
	 *            the registrationUserRoleId to set
	 */
	public void setRegistrationUserRoleID(RegistrationUserRoleID registrationUserRoleID) {
		this.registrationUserRoleID = registrationUserRoleID;
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

}
