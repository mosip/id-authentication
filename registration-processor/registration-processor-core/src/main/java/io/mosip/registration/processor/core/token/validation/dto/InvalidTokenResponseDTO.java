package io.mosip.registration.processor.core.token.validation.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InvalidTokenResponseDTO {
	Errors[] errors;
	String timestamp;
	int status;
}