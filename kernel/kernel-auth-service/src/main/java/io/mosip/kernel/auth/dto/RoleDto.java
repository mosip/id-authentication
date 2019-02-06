package io.mosip.kernel.auth.dto;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
public class RoleDto {

	private String roleId;
	
	private String roleName;
	
	private String roleDescription;
	
	public RoleDto(String roleId, String roleName, String roleDescription){
		this.roleId=roleId;
		this.roleName=roleName;
		this.roleDescription=roleDescription;
	}

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
