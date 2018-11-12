package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;

/**
 * Composite key for RegistrationUserRole entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
public class RegistrationUserRoleID implements Serializable {

	private static final long serialVersionUID = -8072043172665654382L;

	@Column(name = "usr_id", length = 64, nullable = false, updatable = false)
	private String usrId;

	@Column(name = "role_code", length = 32, nullable = false, updatable = false)
	private String roleCode;
	@Column(name = "lang_code", length = 3, nullable = false, updatable = false)
	private String langCode;

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
	 * @return the roleCode
	 */
	public String getRoleCode() {
		return roleCode;
	}

	/**
	 * @param roleCode
	 *            the roleCode to set
	 */
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
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

	
}
