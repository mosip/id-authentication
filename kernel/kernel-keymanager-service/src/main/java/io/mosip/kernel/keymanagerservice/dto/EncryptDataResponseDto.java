package io.mosip.kernel.keymanagerservice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("class which handles keypair")
public class EncryptDataResponseDto {

	/** The encrypted data. */
	@ApiModelProperty(notes = "Encrypted data with private key", required = true)
	private String encryptedData;

}