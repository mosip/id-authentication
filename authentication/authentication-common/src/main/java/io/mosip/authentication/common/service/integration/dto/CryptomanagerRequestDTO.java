package io.mosip.authentication.common.service.integration.dto;

import io.mosip.authentication.core.indauth.dto.BaseAuthRequestDTO;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Crypto-Manager-Request model
 * 
 * @author Arun Bose
 * @author Nagarjuna
 *
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Model representing a Crypto-Manager-Service Request")
public class CryptomanagerRequestDTO extends BaseAuthRequestDTO {

	/**
	 * CryptomanagerRequestDto
	 */
	private CryptomanagerRequestDto request;
}
