package io.mosip.authentication.partnerdemo.service.dto;

import lombok.Data;

@Data
public class EncryptionResponseDto {
	String encryptedSessionKey;
	String encryptedIdentity;
	String requestHMAC;
}
