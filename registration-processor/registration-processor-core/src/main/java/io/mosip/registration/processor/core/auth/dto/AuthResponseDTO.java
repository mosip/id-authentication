package io.mosip.registration.processor.core.auth.dto;

import java.util.List;

import lombok.Data;

@Data
public class AuthResponseDTO extends BaseAuthResponseDTO {

	//private AuthResponseInfo info;
	
	private String transactionID;
	
	private ResponseDTO response; 
	
	private String responseTime;
	
	private String version;
	
	private List<ErrorDTO> errors;

}
