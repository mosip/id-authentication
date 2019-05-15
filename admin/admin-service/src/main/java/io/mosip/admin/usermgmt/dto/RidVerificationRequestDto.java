package io.mosip.admin.usermgmt.dto;

import javax.validation.constraints.NotBlank;

import io.mosip.admin.usermgmt.constants.UserManagementConstants;
import lombok.Data;

@Data
public class RidVerificationRequestDto {
	@NotBlank(message = UserManagementConstants.INVALID_REQUEST)
	private String rid;
	@NotBlank(message = UserManagementConstants.INVALID_REQUEST)
	private String userName;
}
