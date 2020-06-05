package io.mosip.authentication.common.service.integration.dto;

import io.mosip.authentication.core.indauth.dto.BaseAuthResponseDTO;
import io.mosip.kernel.signature.dto.ValidatorResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Class for validator response dto
 * @author Nagarjuna
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ValidatorResponseDTO extends BaseAuthResponseDTO {
	
	private ValidatorResponseDto response;

}
