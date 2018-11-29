package io.mosip.demo.authentication.service.dto;

import lombok.Data;

@Data
public class EncryptionRequestDto {

	private String publicKey;
	private String sessionKey;
	private String identityRequest;

}
