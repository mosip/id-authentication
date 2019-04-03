package io.mosip.kernel.cryptomanager.dto;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyManagerKeyPairResponseDto {

	
	/**
	 * Field for public key
	 */
	@ApiModelProperty(notes = "publicKey in BASE64 encoding format", required = true)
	private String publicKey;
	
	/**
	 * Field for public key
	 */
	@ApiModelProperty(notes = "privateKey in BASE64 encoding format", required = true)
	private String privateKey;

	/**
	 * Key creation time
	 */
	@ApiModelProperty(notes = "Timestamp of issuance of keyPair", required = true)
	private LocalDateTime issuedAt;

	/**
	 * Key expiry time
	 */
	@ApiModelProperty(notes = "Timestamp of expiry of keypair", required = true)
	private LocalDateTime expiryAt;
}
