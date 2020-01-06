package io.mosip.kernel.auth.dto;

import javax.validation.constraints.NotBlank;

import io.mosip.kernel.auth.constant.AuthConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordRequestDto {
	@NotBlank(message=AuthConstant.INVALID_REQUEST)
	private String appId;
	@NotBlank(message=AuthConstant.INVALID_REQUEST)
	private String userName;
	@NotBlank(message=AuthConstant.INVALID_REQUEST)
	private String rid;
	@NotBlank(message=AuthConstant.INVALID_REQUEST)
	private String password;

}
