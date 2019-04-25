package io.mosip.registration.processor.core.token.validation.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TokenResponseDTO {
	
	String id;
	String version;
	String responsetime;
	String metadata;
	Response response;
	Errors[] errors;

}
