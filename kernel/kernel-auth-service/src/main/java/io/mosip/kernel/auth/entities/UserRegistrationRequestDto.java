package io.mosip.kernel.auth.entities;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequestDto {
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
	@JsonFormat(pattern="dd-MM-yyyy")
	private LocalDate dateOfBirth;
	@NotBlank
	private String gender;
	@NotBlank
	private String role;
	@NotBlank
	private String appId;
}
