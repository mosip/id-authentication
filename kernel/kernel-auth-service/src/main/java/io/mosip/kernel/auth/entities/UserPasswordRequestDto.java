package io.mosip.kernel.auth.entities;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordRequestDto {
	@NotBlank
	private String appId;
	@NotBlank
	private String userName;
	@NotBlank
	private String rid;
	@NotBlank
	private String password;

}
