package org.mosip.auth.service.integration.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OtpValidateRequestDTO {

	@NotNull
	private String key;

	@NotNull
	private String Otp;

}
