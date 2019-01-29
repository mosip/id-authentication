package io.mosip.kernel.ldap.entities;

import java.io.Serializable;
import java.util.List;

public class RolesResponseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5863653796023079898L;

	List<RolesDto> roles;

	public List<RolesDto> getRoles() {
		return roles;
	}

	public void setRoles(List<RolesDto> roles) {
		this.roles = roles;
	}

}
