package io.mosip.demo.authentication.service.dto;

import java.util.Map;

import lombok.Data;

@Data
public class EncryptionRequestDto {
	private String tspID;
	private Map<String, Object> identityRequest;

}
