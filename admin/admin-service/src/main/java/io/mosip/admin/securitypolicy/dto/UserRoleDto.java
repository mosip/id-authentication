package io.mosip.admin.securitypolicy.dto;

import lombok.Data;
/**
 * UserRoleDto will contains the user and their roles.
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
public class UserRoleDto{
	/**
	 * field for user id
	 */
	private String userId;
	/**
	 * role of the user
	 */
	private String role;
}
