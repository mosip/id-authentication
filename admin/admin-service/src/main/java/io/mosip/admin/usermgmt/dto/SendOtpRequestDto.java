package io.mosip.admin.usermgmt.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.mosip.admin.usermgmt.constants.UserManagementConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendOtpRequestDto {
	@NotBlank(message = UserManagementConstants.INVALID_REQUEST)
	private String appId;
	@NotBlank(message = UserManagementConstants.INVALID_REQUEST)
	private String context;
	@NotNull
	private List<String> otpChannel;
	@NotBlank(message = UserManagementConstants.INVALID_REQUEST)
	private String userId;
	@NotBlank(message = UserManagementConstants.INVALID_REQUEST)
	private String useridtype;
	@NotBlank(message = UserManagementConstants.INVALID_REQUEST)
	private Object templateVariables;
}
