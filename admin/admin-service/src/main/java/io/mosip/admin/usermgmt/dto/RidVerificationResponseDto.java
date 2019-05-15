package io.mosip.admin.usermgmt.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RidVerificationResponseDto {
	@NotBlank
	private String userName;
}
