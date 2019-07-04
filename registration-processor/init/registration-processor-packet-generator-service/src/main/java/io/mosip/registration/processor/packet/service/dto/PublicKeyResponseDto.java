package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;

import lombok.Data;

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 * @author Rishabh Keshari
 */
@Data
public class PublicKeyResponseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5038613164048257390L;

	/** The public key. */
	private String publicKey;

	/** The issued at. */
	private String issuedAt;

	/** The expiry at. */
	private String expiryAt;

	/**
	 * Instantiates a new public key response dto.
	 *
	 * @param publicKey
	 *            the public key
	 * @param issuedAt
	 *            the issued at
	 * @param expiryAt
	 *            the expiry at
	 */
	public PublicKeyResponseDto(String publicKey, String issuedAt, String expiryAt) {
		this.publicKey = publicKey;
		this.issuedAt = issuedAt;
		this.expiryAt = expiryAt;
	}
	public PublicKeyResponseDto() {
		
	}

}
