package org.mosip.registration.dto;

import java.util.Map;

import lombok.Data;

@Data
public class ResponseDTO {

	private String code;
	private String message;
	private Map<String, String> otherAttributes;
	
}
