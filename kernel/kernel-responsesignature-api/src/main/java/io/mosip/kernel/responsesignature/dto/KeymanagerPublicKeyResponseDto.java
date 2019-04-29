/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.responsesignature.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Key-Manager-Service get-public-key response model
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeymanagerPublicKeyResponseDto {

	/**
	 * Public key in BASE64 encodeding
	 */
	private String publicKey;

	/**
	 * Timestamp of issuance
	 */
	private String issuedAt;

	/**
	 * Timestamp of expiry
	 */
	private String expiryAt;

}
