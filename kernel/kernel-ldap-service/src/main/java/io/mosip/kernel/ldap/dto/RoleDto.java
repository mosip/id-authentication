package io.mosip.kernel.ldap.dto;

import java.io.Serializable;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
public class RoleDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2706412783263468725L;

	private String roleId;
	
	private String roleName;
	
	private String roleDescription;

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
	
	
}
