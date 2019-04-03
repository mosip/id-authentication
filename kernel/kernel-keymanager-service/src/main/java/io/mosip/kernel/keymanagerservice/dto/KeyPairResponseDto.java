package io.mosip.kernel.keymanagerservice.dto;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("class which handles keypair")
public class KeyPairResponseDto<T> {

	/**
	 * Field for public key
	 */
	@ApiModelProperty(notes = "publicKey in BASE64 encoding format", required = true)
	private T publicKey;
	
	/**
	 * Field for public key
	 */
	@ApiModelProperty(notes = "privateKey in BASE64 encoding format", required = true)
	private T privateKey;

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
