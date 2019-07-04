package io.mosip.registration.processor.core.auth.dto;

import lombok.Data;

@Data
public class BaseAuthRequestDTO {

	private boolean consentObtained = true;
	
	private String id;
	
}
