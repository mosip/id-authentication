package io.mosip.authentication.core.indauth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class For IdentityKeyBindingResponseDto extending {@link BaseAuthResponseDTO}
 * 
 * @author Mahammed Taheer
 */

@Data
@EqualsAndHashCode(callSuper=true)
public class IdentityKeyBindingResponseDto extends BaseAuthResponseDTO {
	
	/** The KycResponseDTO */
	private IdentityKeyBindingRespDto response;
	
}
