package io.mosip.admin.usermgmt.dto;

import javax.validation.constraints.NotBlank;

import io.mosip.admin.constant.AdminConstant;
import lombok.Data;

@Data
public class RidVerificationRequestDto {
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String rid;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String userName;
}
