package io.mosip.kernel.cryptomanager.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
public class KeyManagerEncryptResponseDto {

	/** The encrypted data. */
	@ApiModelProperty(notes = "Encrypted data with private key", required = true)
	private String encryptedData;
}
