package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Composite key for RegistrationScreenAuthorization entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
public class RegistrationScreenAuthorizationId implements Serializable {

	private static final long serialVersionUID = -8699602385381298607L;

	@Column(name = "screen_id")
	private String screenId;
	@Column(name = "role_code")
	private String roleCode;

	/**
	 * @return the screenId
	 */
	public String getScreenId() {
		return screenId;
	}

	/**
	 * @param screenId
	 *            the screenId to set
	 */
	public void setScreenId(String screenId) {
		this.screenId = screenId;
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
