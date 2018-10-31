package io.mosip.registration.dto;

import java.util.Map;

import lombok.Data;

@Data
public class SuccessResponseDTO {
	private String code;
	private String message;
	private Map<String, Object> otherAttributes;
	private String infoType;
	
}
