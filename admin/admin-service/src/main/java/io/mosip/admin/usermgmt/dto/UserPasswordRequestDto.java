package io.mosip.admin.usermgmt.dto;

import javax.validation.constraints.NotBlank;

import io.mosip.admin.usermgmt.constants.UserManagementConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordRequestDto {
	@NotBlank(message = UserManagementConstants.INVALID_REQUEST)
	private String appId;
	@NotBlank(message = UserManagementConstants.INVALID_REQUEST)
	private String userName;
	@NotBlank(message = UserManagementConstants.INVALID_REQUEST)
	private String rid;
	@NotBlank(message = UserManagementConstants.INVALID_REQUEST)
	private String password;

}
