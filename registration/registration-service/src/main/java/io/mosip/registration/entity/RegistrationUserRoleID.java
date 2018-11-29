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

	@Column(name = "usr_id")
	private String usrId;
	@Column(name = "role_code")
	private String roleCode;
	
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
	
}
