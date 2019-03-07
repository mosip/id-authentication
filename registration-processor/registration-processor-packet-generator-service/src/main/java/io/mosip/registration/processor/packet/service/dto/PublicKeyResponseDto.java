package io.mosip.registration.processor.packet.service.dto;

import lombok.Data;
@Data
public class PublicKeyResponseDto {
	
	private String publicKey;
	
	private String issuedAt;
	
	private String expiryAt;

	public PublicKeyResponseDto(String publicKey, String issuedAt, String expiryAt) {
		this.publicKey = publicKey;
		this.issuedAt = issuedAt;
		this.expiryAt = expiryAt;
	}
	

}
