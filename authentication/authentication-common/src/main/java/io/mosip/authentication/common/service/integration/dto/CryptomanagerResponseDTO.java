/*
 * 
 * 
 * 
 * 
 */
package io.mosip.authentication.common.service.integration.dto;

import io.mosip.authentication.core.indauth.dto.BaseAuthResponseDTO;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Crypto-Manager-Response model
 * 
 * @author Arun Bose
 * @author Nagarjuna
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ApiModel(description = "Model representing a Crypto-Manager-Service Response")
public class CryptomanagerResponseDTO extends BaseAuthResponseDTO{
	/**
	 * Data Encrypted/Decrypted in BASE64 encoding
	 */
	private CryptomanagerResponseDto response;
}
