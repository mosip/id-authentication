package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;

/**
 * Composite key for RegistrationUserRole entity 
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
public class RegistrationUserRoleID implements Serializable{
	
	private static final long serialVersionUID = -8072043172665654382L;
	
	@Column(name="usr_id", length=64, nullable=false, updatable=false)
	private String usrId;
	@Column(name="role_code", length=32, nullable=false, updatable=false)
	private String roleCode;
	@Column(name="lang_code", length=3, nullable=false, updatable=false)
	private String langCode;

	public String getUsrId() {
		return usrId;
	}
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
}
