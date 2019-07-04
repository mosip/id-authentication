package io.mosip.registration.processor.core.digital.signature.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SignRequestDto {
	@NotBlank
	private String data;
}
