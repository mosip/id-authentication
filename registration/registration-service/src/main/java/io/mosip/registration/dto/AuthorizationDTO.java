package io.mosip.registration.dto;

import java.util.List;
import java.util.Set;

/**
 * This class contains the Registration screen Authorization details.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public class AuthorizationDTO {

	private Set<String> authorizationScreenId;
	private List<String> authorizationRoleCode;
	private boolean authorizationIsPermitted;

	/**
	 * @return the authorizationScreenId
	 */
	public Set<String> getAuthorizationScreenId() {
		return authorizationScreenId;
	}

	/**
	 * @param authorizationScreenId
	 *            the authorizationScreenId to set
	 */
	public void setAuthorizationScreenId(Set<String> authorizationScreenId) {
		this.authorizationScreenId = authorizationScreenId;
	}

	/**
	 * @return the authorizationRoleCode
	 */
	public List<String> getAuthorizationRoleCode() {
		return authorizationRoleCode;
	}

	/**
	 * @param authorizationRoleCode
	 *            the authorizationRoleCode to set
	 */
	public void setAuthorizationRoleCode(List<String> authorizationRoleCode) {
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
