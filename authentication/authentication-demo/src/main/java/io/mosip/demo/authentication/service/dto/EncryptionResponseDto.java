package io.mosip.demo.authentication.service.dto;

import lombok.Data;

@Data
public class EncryptionResponseDto {

	String encryptedSessionKey;
	String encryptedIdentity;

}
