package io.mosip.authentication.demo.dto;

import lombok.Data;

/**
 * The Class EncryptionResponseDto.
 * 
 * @author Sanjay Murali
 */
@Data
public class EncryptionResponseDto {
	String encryptedSessionKey;
	String encryptedIdentity;
	String requestHMAC;
}
