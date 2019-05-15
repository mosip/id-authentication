package io.mosip.authentication.partnerdemo.service.dto;

import java.util.Map;

import lombok.Data;

@Data
public class EncryptionRequestDto {
	
	private Map<String, Object> identityRequest;

}
