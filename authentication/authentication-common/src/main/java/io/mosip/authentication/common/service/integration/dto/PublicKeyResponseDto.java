package io.mosip.authentication.common.service.integration.dto;

import io.mosip.authentication.core.indauth.dto.BaseAuthResponseDTO;
import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Public key response dto
 * @author Nagarjuna
 *
 */

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PublicKeyResponseDto  extends BaseAuthResponseDTO{

	/**
	 * Public key response
	 */
	private PublicKeyResponse<String> response;
}
