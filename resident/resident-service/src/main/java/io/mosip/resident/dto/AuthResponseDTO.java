package io.mosip.resident.dto;

import lombok.Data;

import java.util.List;

@Data
public class AuthResponseDTO extends BaseAuthResponseDTO {

	//private AuthResponseInfo info;
	
	private String transactionID;
	
	private IdAuthResponseDto response;
	
	private String responseTime;
	
	private String version;
	
	private List<ErrorDTO> errors;

}
