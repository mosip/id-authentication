package org.mosip.registration.processor.response;

import java.util.Map;

import lombok.Data;

@Data
public class Response {

	private String code;
	private String message;
	private Map<String, String> otherAttributes;
	
}
