package io.mosip.registration.dto;

import java.util.List;

/**
 * This class contains the Registration screen Authorization details.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public class AuthorizationDTO {

	private List<String> authorizationScreenId;
	private String authorizationRoleCode;
	private boolean authorizationIsPermitted;

	/**
	 * @return the authorizationScreenId
	 */
	public List<String> getAuthorizationScreenId() {
		return authorizationScreenId;
	}

	/**
	 * @param authorizationScreenId
	 *            the authorizationScreenId to set
	 */
	public void setAuthorizationScreenId(List<String> authorizationScreenId) {
		this.authorizationScreenId = authorizationScreenId;
	}

	/**
	 * @return the authorizationRoleCode
	 */
	public String getAuthorizationRoleCode() {
		return authorizationRoleCode;
	}

	/**
	 * @param authorizationRoleCode
	 *            the authorizationRoleCode to set
	 */
	public void setAuthorizationRoleCode(String authorizationRoleCode) {
		this.authorizationRoleCode = authorizationRoleCode;
	}

	/**
	 * @return the authorizationIsPermitted
	 */
	public boolean getAuthorizationIsPermitted() {
		return authorizationIsPermitted;
	}

	/**
	 * @param authorizationIsPermitted
	 *            the authorizationIsPermitted to set
	 */
	public void setAuthorizationIsPermitted(boolean authorizationIsPermitted) {
		this.authorizationIsPermitted = authorizationIsPermitted;
	}

}
