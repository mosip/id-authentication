package io.mosip.resident.dto;

import lombok.Data;

@Data
public class BaseAuthRequestDTO {

	private boolean consentObtained = true;
	
	private String id;
	
}
