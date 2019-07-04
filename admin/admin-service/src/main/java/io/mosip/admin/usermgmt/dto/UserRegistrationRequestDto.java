package io.mosip.admin.usermgmt.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.mosip.admin.constant.AdminConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequestDto {

	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String userName;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String firstName;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String lastName;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String contactNo;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String emailID;
	@JsonFormat(pattern="yyyy-MM-dd")
	@NotNull
	private LocalDate dateOfBirth;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String gender;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String role;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String appId;

	private String ridValidationUrl;
}
