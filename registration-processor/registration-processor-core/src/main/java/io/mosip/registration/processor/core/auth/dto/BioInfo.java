package io.mosip.registration.processor.core.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BioInfo {
	
	private DataInfoDTO data;
	
	private String hash;
	
	private String sessionKey;
	
	private String signature;
	
}
