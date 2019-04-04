package io.mosip.kernel.cryptomanager.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instantiates a new crypto public response dto.
 * 
 * @author Srinivasan
 * @since 1.0.0
 * @param data
 *            the data
 * @param publicKey
 *            the public key
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoEncryptResponseDto {

	/** Data Encrypted/Decrypted in BASE64 encoding. */
	@ApiModelProperty(notes = "Data encrypted/decrypted in BASE64 encoding")
	private String data;

}
