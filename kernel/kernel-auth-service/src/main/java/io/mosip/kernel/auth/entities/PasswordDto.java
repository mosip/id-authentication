package io.mosip.kernel.auth.entities;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class PasswordDto {

	@NotBlank
	@Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,})",message="password invalid")
	private String oldPassword;

	@NotBlank
	@Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,})",message="password invalid")
	private String newPassword;

	@NotBlank
	private String userId;
	
	private String hashAlgo;
}
