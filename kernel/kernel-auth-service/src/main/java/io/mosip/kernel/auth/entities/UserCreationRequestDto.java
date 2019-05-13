package io.mosip.kernel.auth.entities;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationRequestDto {
	@NotBlank
	private String userName;
	@NotBlank
	private String firstName;
	@NotBlank
	private String lastName;
	@NotBlank
	private String contactNo;
	@NotBlank
	private String emailID;
	@NotNull
	private LocalDate dateOfBirth;
	@NotBlank
	private String gender;
	@NotBlank
	private String role;
	@NotBlank
	private String appId;
}
