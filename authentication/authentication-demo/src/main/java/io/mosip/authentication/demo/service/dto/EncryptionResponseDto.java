package io.mosip.authentication.demo.service.dto;

import lombok.Data;

@Data
public class EncryptionResponseDto {
	String encryptedSessionKey;
	String encryptedIdentity;

}
