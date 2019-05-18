package io.mosip.admin.usermgmt.dto;

import javax.validation.constraints.NotBlank;

import io.mosip.admin.constant.AdminConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordRequestDto {
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String appId;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String userName;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String rid;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String password;

}
