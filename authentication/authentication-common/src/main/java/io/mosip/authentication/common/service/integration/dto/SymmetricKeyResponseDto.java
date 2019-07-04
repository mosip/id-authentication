package io.mosip.authentication.common.service.integration.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for symmetric Key
 * 
 * @author Arun Bose
 * 
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Class representing a Decrypt Response")
public class SymmetricKeyResponseDto {

	/**
	 * The string symmetric Key
	 */
	@ApiModelProperty(notes = "Decrypted Data in BASE64 encoding", required = true)
	private String symmetricKey;

}
